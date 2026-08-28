# Spec — Autorização de rotas via permissões IRN

**Aplicação:** iGRP Platform Process Manager Studio (`cv.igrp.platform:process_manager_studio`)
**Estado:** proposta, por implementar
**Repos envolvidos:** este + `igrp-process-management-backend-monorepo` (branch `feature/irn-system-administration-integration`)
**Documento irmão:** `igrp-process-management-backend-api/docs/SPEC_ROUTE_AUTHORIZATION.md` — mesma solução, catálogo próprio

---

## 1. Contexto e problema

O `SecurityConfig` desta aplicação abre **todos os GET a pedidos anónimos**:

```java
http.authorizeHttpRequests((authorize) -> authorize
        .requestMatchers(HttpMethod.GET).permitAll()   // <- sem token nenhum
        .anyRequest().authenticated()
);
```

Ficam acessíveis sem qualquer autenticação, entre outros:

- `GET /api/v1/projects` — lista todos os projetos
- `GET /api/v1/projects/{projectId}` · `/deployed-process` · `/history-process`
- `GET /api/v1/projects/process-definitions` e `/{processId}` — diagramas BPMN
- `GET /api/v1/projects/process-definitions/{processId}/variables` — variáveis de processo

O resto (POST/PUT/PATCH) exige apenas *estar autenticado*: **qualquer** token válido do realm pode
criar, editar, desativar e fazer **deploy** de process definitions. Não há `@EnableMethodSecurity` nem
nenhum `@PreAuthorize`.

Esta aplicação também não tem, hoje, nenhuma peça de integração IRN: sem `process-runtime-auth-*`, sem
`IAuthorizationServiceAdapter`, sem cache, e com o `JwtGrantedAuthoritiesConverter` stock (lê só as
roles do JWT do Keycloak). Toda a base de autorização tem de ser trazida.

É o equivalente, nesta aplicação, ao P0 documentado na API de management
(`igrp-process-management-backend-api/docs/SECURITY_RECOMMENDATIONS.md`):

> **P0 · Task/process access** — *Enforce authorization on task search, claim, assign, complete, import,
> deploy, and admin-style operations.*

### 1.1 Restrições apuradas

**Os controllers são gerados.** `ProjectController` e `ProcessDefinitionController` começam com:

```
/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */
```

Anotar controllers com `@PreAuthorize` é o sítio errado — a próxima regeneração apaga tudo. As regras
têm de viver fora do código gerado.

**O iGRP Studio não é alternativa.** O motor de permissões está por implementar:
`igrp-studio-ide/src/main/engines/SpringEngine.ts:32` tem `createPermission()` a lançar
`Error('Method not implemented.')`, e `.igrpstudio/**/controllers/*.json` não tem nenhum campo de
autorização. É por isso que o `.igrpstudio/permissions.json` deste projeto está `[]`.

**O mapeamento tem de ser configurável.** A mesma solução aplica-se à Process Management API, que tem
base paths e módulos diferentes. A implementação fica única, no módulo partilhado, alimentada por
configuração de cada aplicação.

---

## 2. Objetivo

Impor autenticação e autorização por permissão em todas as rotas desta API, usando o array `permissions`
devolvido pelo `/Auth/me` do IRN, sem tocar em código gerado e sem duplicar a tabela de rotas em cada
aplicação.

### Decisões tomadas

| # | Decisão |
|---|---|
| D-1 | Usar **só** o array `permissions` do `/Auth/me`. `accessibleModuleCodes` fica de fora. |
| D-2 | Catálogo novo, derivado dos métodos HTTP, registado no System Administration. O catálogo mantém-se, mas desde a 24.5 cada rota **também** aceita os códigos reais dos frontends IRN via `accept-also` (secção 4.3), porque na prática só os códigos dos módulos frontend estão atribuídos a perfis. |
| D-3 | O mapeamento rota→permissão **não vive no `SecurityConfig`** — é delegado ao adapter IRN por uma interface no `process-runtime-auth-core`. |
| D-4 | A mesma solução aplica-se à `igrp-process-management-backend-api`. |
| D-5 | Não declarar o catálogo no iGRP Studio por agora. |
| D-6 | Híbrido multi-frontend: cada rota aceita qualquer de — a permissão derivada ou os códigos reais dos frontends (`accept-also`), a partir da 24.5. |

---

## 3. Requisitos funcionais

**RF-1** — Nenhuma rota desta API é acessível sem autenticação. O `.requestMatchers(HttpMethod.GET).permitAll()`
é removido.

**RF-2** — O `SecurityConfig` não pode conter nenhuma rota de negócio literal. As regras são obtidas de
uma abstração injetada.

**RF-3** — O módulo `process-runtime-auth-core` expõe uma interface `IRouteAuthorizationAdapter` com as
regras de rota e a política para rotas não cobertas, seguindo o padrão já existente de
`IAuthorizationServiceAdapter` (interface + implementação `Default*` com `@ConditionalOnProperty`).

**RF-4** — A implementação IRN dessa interface deriva as regras de configuração da aplicação
(`irn.authorization.routes.*`), pela ordem declarada. Uma implementação serve as duas aplicações.

**RF-5** — O identificador do chamador passa a ser enriquecido pelo `IAuthorizationServiceAdapter`
(grupos, permissões e estatuto de super admin), em vez do `JwtGrantedAuthoritiesConverter` stock. Com o
adapter IRN isso significa resolver o cookie `session_id` contra o `/Auth/me`.

**RF-6** — `IrnAuthorizationCacheService.getPermissions()` passa a devolver as `permissions` do
`/Auth/me`. O `IrnMeResponse` não leva campos novos.

**RF-7** — O `GET /Auth/me` é chamado **no máximo uma vez por pedido HTTP**, com cache por `sessionId`.

**RF-8** — Rotas não cobertas por nenhuma regra são **negadas** (fail closed) quando o adapter IRN está
ativo, para que um controller novo gerado pelo Studio não entre desprotegido.

**RF-9** — Com `igrp.authorization.service.adapter=default` (deployments não-IRN) a aplicação continua a
arrancar e a servir, sem regras de permissão — mas **sem** o `GET permitAll` (RF-1 é incondicional).

**RF-10** — O super admin (`ROLE_DEPT_IGRP.superadmin`) passa em todas as regras, sem ter de constar de
cada uma.

**RF-11** — Falha no enriquecimento de authorities não pode conceder privilégios: sem role de
administrador no fallback, e log ao nível de alerta.

---

## 4. Catálogo de permissões

Um módulo por área de rotas, ação derivada do método HTTP, no formato IRN `MODULO:acao`.

| Método HTTP | Ação |
|---|---|
| `GET` | `visualizar` |
| `POST` | `criar` |
| `PUT` / `PATCH` | `editar` |
| `DELETE` | `eliminar` |

Os códigos levam prefixo `STUDIO_`: as duas aplicações partilham o mesmo System Administration e ambas
têm um "process definitions" — mas são coisas diferentes (aqui é o desenho BPMN; lá é o processo
publicado no motor).

Registam-se só as combinações que existem — **8 permissões**:

| Módulo | Permissões |
|---|---|
| `STUDIO_PROJECTS` | `:visualizar` · `:criar` · `:editar` |
| `STUDIO_PROCESS_DEFINITIONS` | `:visualizar` · `:criar` · `:editar` · `:publicar` |
| `STUDIO_PARAMETERIZATION` | `:visualizar` |

`STUDIO_PROCESS_DEFINITIONS:publicar` é a única ação **não** derivada do método: cobre o deploy do BPMN
para o motor de processos, a operação mais sensível desta API. Não pode partilhar permissão com guardar
um rascunho. O IRN já usa ações nomeadas assim (`GUIAS:validar`, `PEDIDOS:self_assign`,
`MODULOS_APLICACIONAIS:gerir_dependencias`), portanto é idiomático.

> `:publicar` **não** implica `:criar`, nem o contrário — são independentes. Quem só tem `:publicar` faz
> deploy mas não desenha; quem só tem `:criar` desenha tudo menos publicar. É deliberado: permite separar
> quem modela processos de quem os põe em produção.

Nenhum módulo tem `:eliminar` — não existe nenhum `@DeleteMapping` nesta API. O *delete* de uma process
definition é `PATCH .../delete` (soft delete), logo cai em `:editar`.

### 4.1 Ordem dos padrões — obrigatória

`ProjectController` e `ProcessDefinitionController` partilham `@RequestMapping(path = "api/v1/projects")`.
`/api/v1/projects` é prefixo dos padrões de process-definitions e engoli-los-ia se viesse primeiro. A
ordem é parte da especificação:

| Ordem | Padrão | Módulo |
|---|---|---|
| 1 | `/api/v1/projects/process-definitions` | `STUDIO_PROCESS_DEFINITIONS` |
| 2 | `/api/v1/projects/*/process-definitions` | `STUDIO_PROCESS_DEFINITIONS` |
| 3 | `/api/v1/projects` | `STUDIO_PROJECTS` |
| 4 | `/parameterization` | `STUDIO_PARAMETERIZATION` |

### 4.2 Mapa completo rota → permissão

**`STUDIO_PROJECTS`** — `ProjectController`

```
GET    /api/v1/projects                                  STUDIO_PROJECTS:visualizar   getProjects
GET    /api/v1/projects/{projectId}                      STUDIO_PROJECTS:visualizar   getProjectById
GET    /api/v1/projects/{projectId}/deployed-process     STUDIO_PROJECTS:visualizar   getDeployedProcessByProjectId
GET    /api/v1/projects/{projectId}/history-process      STUDIO_PROJECTS:visualizar   getProcessHistoryByProjectId
POST   /api/v1/projects                                  STUDIO_PROJECTS:criar        createProject
PUT    /api/v1/projects/{projectId}                      STUDIO_PROJECTS:editar       updateProject
PATCH  /api/v1/projects/{projectId}/enable               STUDIO_PROJECTS:editar       enableProject
PATCH  /api/v1/projects/{projectId}/disable              STUDIO_PROJECTS:editar       disableProject
```

**`STUDIO_PROCESS_DEFINITIONS`** — `ProcessDefinitionController`

```
GET    …/projects/process-definitions                        :visualizar   getProcessDefinition
GET    …/projects/process-definitions/{processId}            :visualizar   getProcessDefinitionById
GET    …/projects/process-definitions/{processId}/variables  :visualizar   getProcessDefinitionVariables
POST   …/projects/{projectId}/process-definitions            :criar        saveProcessDefinition
POST   …/projects/process-definitions/{processKey}/deploy    :publicar     deployProcessDefinition   (override)
POST   …/projects/process-definitions/{processId}/variables  :criar        addVariablesToProcess
PUT    …/projects/process-definitions/{processKey}/diagram   :editar       diagramEditorProcessDefinition
PUT    …/projects/process-definitions/{processId}            :editar       updateProcessDefinition
PATCH  …/projects/process-definitions/{processId}/delete     :editar       deleteProcessDefinition   (ver risco R-1)
PATCH  …/projects/process-definitions/{processId}/restore    :editar       restoreProcessDefinition
```

**`STUDIO_PARAMETERIZATION`** — `ParameterizationController` (não é gerado pelo Studio)

```
GET    /parameterization/process-definition-state            :visualizar   getProcessDefinitionState
```

### 4.3 Frontends e códigos IRN aceites (`accept-also`)

Desde `process-runtime-auth-irn` `0.1.0-beta.24.5`.

**Problema.** Vários frontends IRN partilham os mesmos endpoints deste backend, mas cada um é um *módulo
IRN distinto, com o seu próprio código e os seus próprios verbos de ação*. O backend não consegue saber
qual frontend fez a chamada — mesmo token, mesmo endpoint. Na prática, no System Administration só os
códigos dos módulos frontend estão atribuídos a perfis; o catálogo derivado `STUDIO_*:acao` não foi
adotado. Gate feito só pelo catálogo derivado daria **403 a toda a gente**.

**Solução (híbrido `accept-also`).** Cada nível de rota passa a aceitar **qualquer de**: a permissão
derivada (`CODE:acao`) **ou** as permissões reais dos frontends IRN, declaradas em configuração como
`accept-also.<acao>` — uma lista, any-of, indexada pela ação derivada, para que os overrides de leitura
herdem a lista de `visualizar`. O SPI já suportava múltiplas authorities (`RouteAuthorizationRule.anyAuthority`
é um `Set`, o `SecurityConfig` chama `hasAnyAuthority`), por isso o `SecurityConfig` **não muda**. Ausente
ou vazio → comportamento idêntico ao anterior (retrocompatível).

Exemplo, num módulo Studio (linha comentada no `application.properties`):

```properties
# accept-also da leitura de process-definitions — descomentar e preencher com o código real do frontend
# irn.authorization.routes.modules[0].accept-also.visualizar=${IRN_STUDIO_PD_ACCEPT_READ:}
```

Os placeholders estão **preparados em todos os módulos Studio**, cada um com o seu knob de env-var e um
TODO; só falta descomentar o nível certo e preencher o código. Candidatos para o Studio:
`PROCESS_CONFIGURATION`, `CONFIGURADOR_PROCESSOS` (por confirmar). O único código confirmado em qualquer
sítio é `TASK_MANAGEMENT:ver`, e esse é da API de management (tasks), não do Studio — por isso **nenhuma
lista `accept-also` do Studio está ativa ainda**; o mecanismo está ligado e à espera.

**Deploy Studio→Runtime — dois gates, um só token.** O `POST /api/v1/projects/process-definitions/{key}/deploy`
exige `STUDIO_PROCESS_DEFINITIONS:publicar` (override, não o derivado `:criar`). O handler de deploy do
Studio reencaminha os headers do pedido — incluindo o `Authorization: Bearer` do utilizador (só se retiram
headers de transporte como `content-length`/`host`) — para o motor de processos em `IGRP_PROCESS_ENGINE_BASE_URL`,
que reaplica o **seu próprio** gate `PROCESS_DEFINITIONS:publicar`. Mesma identidade real de ponta a ponta,
sem service account. Verificado no stack e2e: um perfil com `STUDIO_PROCESS_DEFINITIONS:publicar` passa o
gate (depois 400 em body vazio, ou seja, não é falha de autorização); um perfil com `:visualizar`+`:criar`
mas **sem** `:publicar` leva **403** no Studio, antes de o runtime sequer ser chamado — prova de que `:criar`
não faz deploy.

### 4.4 Chamadores de máquina (M2M, release 24.6)

Este spec cobre o caminho de **utilizador** (JWT Keycloak + sessão IRN). Sistemas externos sem sessão
autenticam por **API key M2M** — `Authorization: Bearer igrpm2m_…` — com keys **próprias do Studio**
(tabela e gestão independentes das da management API), cujas permissões `MODULO:acao` passam nas
mesmas regras de rota desta spec. As rotas `/m2m-keys/**` têm gate dedicado no `SecurityConfig`
(JWT super-admin only), fora do catálogo. Spec completo no repo da management API:
`docs/SPEC_M2M_AUTHORIZATION.md`.

### 4.5 Riscos assinalados

| # | Risco | Correção, se decidirem |
|---|---|---|
| R-1 | `PATCH …/{processId}/delete` é um soft delete mas cai em `:editar`, não `:eliminar` | ação `:eliminar` por override, se quiserem separar quem edita de quem apaga |
| R-2 | `PATCH …/{projectId}/enable` e `/disable` caem em `:editar`, iguais a renomear um projeto | idem |

*(O deploy deixou de ser risco: passou a ter `:publicar` própria — secção 4.2.)*

---

## 5. Desenho técnico

### 5.1 `process-runtime-auth-core` (monorepo) — a interface

Pacote `cv.igrp.framework.process.runtime.auth.core.adapter`, ao lado do `IAuthorizationServiceAdapter`.

```java
public record RouteAuthorizationRule(
        HttpMethod method,        // null = qualquer método
        String pattern,           // ex. "/api/v1/projects/**"
        Set<String> anyAuthority  // basta uma → hasAnyAuthority(...)
) {}

public interface IRouteAuthorizationAdapter {
    /** Ordem importa: a primeira regra que casa decide. */
    List<RouteAuthorizationRule> getRules();

    /** true → rotas não cobertas por nenhuma regra são negadas (fail closed). */
    boolean denyUnmatched();
}
```

`DefaultRouteAuthorizationAdapter` — mesmo padrão do `DefaultAuthorizationServiceAdapter`:

```java
@Component
@ConditionalOnProperty(name = "igrp.authorization.service.adapter",
                       havingValue = "default", matchIfMissing = true)
```

`getRules()` → `List.of()`, `denyUnmatched()` → `false`. Satisfaz RF-9.

`CoreAuthorizationAutoConfiguration` já faz `@ComponentScan` do pacote — nada a registar de novo.

`HttpMethod` vem de `org.springframework.http` (spring-web). O `pom.xml` do `auth-core` declara hoje
`jakarta.servlet-api`, `slf4j-api`, `spring-context`, `spring-boot-autoconfigure` e
`spring-security-oauth2-jose` — acrescentar **`spring-web` como `provided`**.

### 5.2 `process-runtime-auth-irn` (monorepo) — permissões e regras

**Permissões (RF-6, RF-7).** `IrnAuthorizationCacheService` tem dois defeitos:

1. `getPermissions()` (linhas ~63-86) devolve `Set.of()` com o código real comentado. Descomentar →
   `new HashSet<>(irnMeResponse.permissions())`.
2. `client.getMe()` é chamado três vezes por pedido (`getGroups`, `getPermissions`, `isSuperAdmin` têm
   `@Cacheable` cada um, mas não há cache sobre o `getMe`). Introduzir um bean novo com
   `@Cacheable(value = "irnMeCache", key = "#sessionId") IrnMeResponse me(String sessionId)` e fazer os
   três métodos lerem dele.

O `@Cacheable` só funciona via proxy Spring, logo o `me()` tem de ficar **noutro bean** — é exatamente a
razão pela qual o `IrnAuthorizationCacheService` já está separado do `IrnAuthorizationServiceAdapter`.
Manter `unless = "#result.isEmpty()"` para não cachear falhas.

**Regras (RF-4).**

```java
@ConfigurationProperties(prefix = "irn.authorization.routes")
public record IrnRouteProperties(
        @DefaultValue("true") boolean denyUnmatched,
        List<ModuleRoutes> modules) {

    public record ModuleRoutes(
            String code,                // "STUDIO_PROCESS_DEFINITIONS"
            String pattern,             // "/api/v1/projects"  (ordem = a da lista)
            List<Override> overrides) {

        /** Rota que foge à regra do método: {método, sufixo do path, ação explícita}. */
        public record Override(HttpMethod method, String path, String action) {}
    }
}
```

```java
@Component
@ConditionalOnProperty(name = "igrp.authorization.service.adapter", havingValue = "irn")
public class IrnRouteAuthorizationAdapter implements IRouteAuthorizationAdapter { ... }
```

`getRules()` percorre `modules` **pela ordem declarada** e, para cada módulo, emite:

1. os `overrides` → `{método, pattern + path, code + ":" + action}`;
2. uma regra por método sobre `pattern` **e** `pattern + "/**"`, com o verbo da tabela da secção 4
   (`GET→visualizar`, `POST→criar`, `PUT/PATCH→editar`, `DELETE→eliminar`).

Os overrides vêm sempre antes das genéricas do mesmo módulo — é aí que está o conflito, e a primeira
regra que casa decide. É o mecanismo que dá o `:publicar` ao deploy, e o mesmo que a API de management usa
para as leituras feitas por POST: acrescentar uma ação nova é uma entrada de configuração, nunca uma
alteração de código.

As duas variantes de padrão são necessárias porque o `ProjectController` tem mappings sem `value`
(`POST` e `GET` na raiz `api/v1/projects`).

**Híbrido multi-frontend (24.5, secção 4.3).** O `ModuleRoutes` ganhou o componente
`acceptAlso` (`Map<String, List<String>>`), indexado pela ação derivada. O helper
`authoritiesFor(module, action)` une `code:action` com `acceptAlso.get(action)` e devolve o conjunto
completo de authorities aceites por essa ação — os overrides herdam a lista pela sua ação (um override de
leitura herda a de `visualizar`). Mapa ausente ou vazio → só a authority derivada, idêntico ao anterior.

Registar o record com `@EnableConfigurationProperties` no `IRNAuthorizationAutoConfiguration`, seguindo
o mecanismo que já regista o `IrnApiProperties`.

### 5.3 `pom.xml` desta aplicação

Importar o `process-runtime-bom` e acrescentar:

- `process-runtime-auth-core` — a interface e o adapter default
- `process-runtime-auth-irn` — o adapter IRN e as regras
- `process-runtime-irn-integration` — fornece o `RestClient` assinado em RS256 de que o `IrnAuthClient`
  depende (`igrp.restclient.provider=irn`)
- `spring-boot-starter-cache` — o `com.github.ben-manes.caffeine:caffeine` **já está** no `pom.xml`
  (linhas 165-166), mas o starter de cache não

Acrescentar `@EnableCaching` em `IgrpPlatformProcessManagerStudioApplication` — não existe hoje em lado
nenhum. Sem isso os `@Cacheable` do adapter IRN são ignorados e cada pedido faz três chamadas ao
`/Auth/me`.

O `spring-boot-starter-actuator` já está presente (linha 49) e não há nenhuma propriedade `management.*`,
por isso o `/actuator/health` fica no caminho por omissão — é o que a regra `permitAll` de 5.4 liberta.

Manter a compatibilidade de versões: o monorepo está em Spring Boot 3.5.x e Java 25 — confirmar contra o
parent desta aplicação antes de subir o BOM.

### 5.4 `shared/security/SecurityConfig.java`

Quatro mudanças:

**1. Remover o `GET permitAll` (RF-1).**

```java
- .requestMatchers(HttpMethod.GET).permitAll()
```

**2. Substituir o converter stock pelo enriquecido (RF-5).** O `JwtGrantedAuthoritiesConverter` atual lê
apenas as roles do JWT. Passa a usar-se o mesmo padrão da Process Management API: obter o
`HttpServletRequest` do `RequestContextHolder` e passar `(token, request)` ao
`IAuthorizationServiceAdapter` — é assim que o adapter IRN chega ao cookie `session_id`:

```java
converter.setJwtGrantedAuthoritiesConverter(jwt -> {
    HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(
        RequestContextHolder.getRequestAttributes())).getRequest();
    Set<GrantedAuthority> authorities = new HashSet<>();
    final String token = jwt.getTokenValue();
    try {
        authorizationService.getActiveGroups(token, request)
            .forEach(g -> authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + g)));
        authorizationService.getPermissions(token, request)
            .forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
        if (authorizationService.isSuperAdmin(token, request)) {
            authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + SUPER_ADMIN_ROLE));
        }
    } catch (Exception e) {
        LOGGER.error("SECURITY: falha ao enriquecer authorities [sub={}]", jwt.getSubject(), e);
        // fail closed — nenhuma authority concedida (RF-11)
    }
    return authorities;
});
```

Ao contrário da API de management, aqui **não** há Activiti, portanto não é preciso nenhuma role mínima
de fallback: o `catch` não concede nada.

**3. Desativar o CSRF.** O `csrf disable` original estava num bloco comentado — o CSRF sempre esteve
ativo, e com ele qualquer POST/PUT/PATCH leva 403 **antes** das regras de permissão serem consultadas.
API stateless de bearer tokens não usa tokens CSRF; o process runtime também o desativa.

**4. Aplicar as regras (RF-2, RF-8, RF-10).** O construtor passa a receber
`IRouteAuthorizationAdapter routeAuthorization`:

```java
.authorizeHttpRequests(authorize -> {
    authorize.requestMatchers(r -> r.getDispatcherType() == DispatcherType.ERROR).permitAll();
    authorize.requestMatchers(
        "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
        "/swagger-resources/**", "/webjars/**",
        "/actuator/health", "/actuator/health/**").permitAll();

    routeAuthorization.getRules().forEach(rule ->
        (rule.method() == null
            ? authorize.requestMatchers(rule.pattern())
            : authorize.requestMatchers(rule.method(), rule.pattern()))
        .hasAnyAuthority(withSuperAdmin(rule.anyAuthority())));

    if (routeAuthorization.denyUnmatched()) authorize.anyRequest().denyAll();
    else authorize.anyRequest().authenticated();
})
```

`withSuperAdmin(...)` é um helper privado que anexa `ROLE_ + SUPER_ADMIN_ROLE` a cada regra (RF-10).

O `permitAll` do dispatcher `ERROR` é **obrigatório** com `denyAll()`: sem ele os erros do Spring viram
403 em vez do estado real.

`hasAnyAuthority` aceita `:` sem escaping — `SimpleGrantedAuthority` é apenas uma string.

**A rever antes de fechar as rotas:** o bean `jwtDecoder` está anotado com
`@Profile("!development & !staging")`. Em dev e staging não existe `JwtDecoder` explícito, o que torna o
arranque dependente de auto-configuração a partir do `issuer-uri`. Confirmar o comportamento nesses
perfis, senão o ambiente de desenvolvimento fica inutilizável assim que o `GET permitAll` desaparecer.

### 5.5 Componentes a reutilizar

- `IAuthorizationServiceAdapter` (`auth-core`) — já expõe `getGroups` / `getPermissions` /
  `isSuperAdmin` / `getActiveGroups` e recebe o `HttpServletRequest`, que é o que permite ao adapter IRN
  chegar ao cookie `session_id`.
- `IrnAuthClient` + `IrnApiProperties` — o cliente `/Auth/me` já existe e funciona.
- `RestClientSignedAuthorizationConfig` / `JwtTokenService` (`process-runtime-irn-integration`) — o JWT
  RS256 service-to-service já é injetado no `RestClient` por interceptor. **Não escrever assinatura nova.**
- O `SecurityConfig` da Process Management API é o modelo literal do converter enriquecido (5.4, ponto 2).

### 5.6 Ficheiros afetados

**Monorepo** (`feature/irn-system-administration-integration`)

| Ficheiro | Ação |
|---|---|
| `auth-core/.../adapter/RouteAuthorizationRule.java` | novo |
| `auth-core/.../adapter/IRouteAuthorizationAdapter.java` | novo |
| `auth-core/.../adapter/DefaultRouteAuthorizationAdapter.java` | novo |
| `auth-core/pom.xml` | + `spring-web` (provided) |
| `auth-irn/.../adapter/IrnRouteProperties.java` | novo |
| `auth-irn/.../adapter/IrnRouteAuthorizationAdapter.java` | novo |
| `auth-irn/.../adapter/IrnAuthorizationCacheService.java` | cachear `me()`, ativar permissões |
| `auth-irn/.../IRNAuthorizationAutoConfiguration.java` | `@EnableConfigurationProperties` |
| `pom.xml` (raiz) + `process-runtime-bom/pom.xml` | bump de versão; o BOM tem de listar `auth-irn` e `irn-integration` |

**Esta aplicação** (`feacture/security-harding`)

| Ficheiro | Ação |
|---|---|
| `pom.xml` | BOM + `auth-core` / `auth-irn` / `irn-integration` + cache (5.3) |
| `shared/security/SecurityConfig.java` | remover o `GET permitAll`, converter enriquecido, regras (5.4) |
| `IgrpPlatformProcessManagerStudioApplication` (classe da aplicação) | `@EnableCaching` |
| `src/main/resources/application*.properties` · `.env.example` | secção 6 |
| *(teste HTTP de rotas: pendente — requer Testcontainers; ver CA-3)* | — |

**Nenhum controller é tocado, e nenhuma rota de negócio fica hardcoded no `SecurityConfig`.**

---

## 6. Configuração

```properties
igrp.authorization.service.adapter=${IGRP_AUTHORIZATION_SERVICE_ADAPTER:default}
igrp.restclient.provider=${IGRP_RESTCLIENT_PROVIDER:}
igrp.authorization.jwt.key=${IGRP_AUTHORIZATION_JWT_KEY:default}
igrp.authorization.jwt.private-key=${IGRP_AUTHORIZATION_JWT_PRIVATE_KEY:default}

irn.api.base-url=${IRN_API_BASE_URL:}
irn.api.super-admin-email=${IRN_API_SUPER_ADMIN_EMAIL:}
irn.api.session-cookie-name=${IRN_API_SESSION_COOKIE_NAME:session_id}

spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=10000,expireAfterWrite=5m

irn.authorization.routes.deny-unmatched=true
irn.authorization.routes.modules[0].code=STUDIO_PROCESS_DEFINITIONS
irn.authorization.routes.modules[0].pattern=/api/v1/projects/process-definitions
irn.authorization.routes.modules[0].overrides[0].method=POST
irn.authorization.routes.modules[0].overrides[0].path=/*/deploy
irn.authorization.routes.modules[0].overrides[0].action=publicar
irn.authorization.routes.modules[1].code=STUDIO_PROCESS_DEFINITIONS
irn.authorization.routes.modules[1].pattern=/api/v1/projects/*/process-definitions
irn.authorization.routes.modules[2].code=STUDIO_PROJECTS
irn.authorization.routes.modules[2].pattern=/api/v1/projects
irn.authorization.routes.modules[3].code=STUDIO_PARAMETERIZATION
irn.authorization.routes.modules[3].pattern=/parameterization

# accept-also — códigos reais dos frontends IRN por nível (secção 4.3). Comentados: placeholders prontos,
# cada módulo com o seu knob de env-var. Descomentar o nível certo e preencher quando o código vier do
# System Administration — sem rebuild.
# irn.authorization.routes.modules[0].accept-also.publicar=${IRN_STUDIO_PD_ACCEPT_PUBLISH:}
# irn.authorization.routes.modules[0].accept-also.visualizar=${IRN_STUDIO_PD_ACCEPT_READ:}
# irn.authorization.routes.modules[1].accept-also.visualizar=${IRN_STUDIO_PD_ACCEPT_READ:}
# irn.authorization.routes.modules[2].accept-also.visualizar=${IRN_STUDIO_PROJ_ACCEPT_READ:}
# irn.authorization.routes.modules[2].accept-also.criar=${IRN_STUDIO_PROJ_ACCEPT_WRITE:}
# irn.authorization.routes.modules[3].accept-also.visualizar=${IRN_STUDIO_PARAM_ACCEPT_READ:}
```

Em ambiente IRN: `IGRP_AUTHORIZATION_SERVICE_ADAPTER=irn` e `IGRP_RESTCLIENT_PROVIDER=irn`.

Para ativar um `accept-also`: descomentar a linha do nível pretendido e pôr o código real do frontend no
env-var correspondente (lista separada por vírgulas se for mais do que um). Enquanto ficarem comentados, o
gate corre só pelo catálogo derivado — hoje nenhuma lista está ativa (secção 4.3).

**A ordem `[0] → [1] → [2]` é obrigatória** (secção 4.1): `/api/v1/projects` é prefixo dos dois primeiros
e engoliria as rotas de process-definitions se viesse antes.

Chaves e formato da chave privada RSA (PKCS#1) documentados em `IRN-CUSTOMIZATION.md` do monorepo.

---

## 7. Registo no System Administration do IRN

**Não existe API de registo.** A varredura aos repos não encontrou nenhum cliente HTTP a apontar para
`system-administration`, `access-management`, `exp-cvt-system-administration` ou qualquer host IRN — o
único egress desta aplicação é para o motor de processos (`igrp.process.engine.base-url`). As permissões
são consumidas, nunca publicadas.

O registo é **manual, na UI do System Administration**:

1. Criar os módulos `STUDIO_PROJECTS`, `STUDIO_PROCESS_DEFINITIONS`, `STUDIO_PARAMETERIZATION`.
2. Criar as 8 permissões da secção 4.
3. Associá-las aos perfis.

A partir daí aparecem no array `permissions` do `/Auth/me` e o gate funciona.

> **`STUDIO_PROCESS_DEFINITIONS:publicar`** publica BPMN executável no motor de processos. Deve ser um
> perfil próprio, não um extra do perfil de quem desenha processos.

A tabela da secção 4 é o documento que se leva para o System Administration.

---

## 8. Critérios de aceitação

**CA-1 · Build** — `mvn -q install` no monorepo, depois `mvn -q -DskipTests package` nesta aplicação.

**CA-2 · Fim do acesso anónimo (RF-1)** — `GET /api/v1/projects` **sem** token devolve **401**. Idem para
`GET /api/v1/projects/process-definitions` e `/{processId}/variables`.

**CA-3 · Regras de rota** — a derivação e a ordem das regras estão cobertas por
`IrnRouteAuthorizationAdapterTest` no monorepo. O teste HTTP completo fica pendente (requer o contexto
real); até lá, validar em DSV com a tabela seguinte:

| Authorities | Pedido | Esperado |
|---|---|---|
| `STUDIO_PROJECTS:visualizar` | `GET /api/v1/projects` | passa o filtro de segurança |
| `STUDIO_PROJECTS:visualizar` | `POST /api/v1/projects` | **403** |
| `STUDIO_PROJECTS:visualizar` | `GET /api/v1/projects/process-definitions` | **403** — prova a ordem dos padrões (4.1) |
| `STUDIO_PROCESS_DEFINITIONS:visualizar` | `GET /api/v1/projects/process-definitions` | passa |
| `STUDIO_PROCESS_DEFINITIONS:criar` | `POST /api/v1/projects/process-definitions/{k}/deploy` | **403** — prova que `:publicar` é independente |
| `STUDIO_PROCESS_DEFINITIONS:publicar` | `POST /api/v1/projects/process-definitions/{k}/deploy` | passa |
| `STUDIO_PROCESS_DEFINITIONS:publicar` | `POST /api/v1/projects/{id}/process-definitions` | **403** |
| `STUDIO_PROCESS_DEFINITIONS:visualizar` | `PATCH /api/v1/projects/process-definitions/{id}/delete` | **403** |
| *(nenhuma)* | qualquer rota de negócio | **403** |
| `ROLE_DEPT_IGRP.superadmin` | qualquer rota | passa |
| qualquer | `GET /rota-inexistente` | **403** — prova o `denyUnmatched` |
| qualquer | `GET /actuator/health` | **200** |

**CA-4 · Sem regressão (RF-9)** — com `igrp.authorization.service.adapter=default` a aplicação arranca e
serve; nenhuma rota é negada por falta de permissão, mas `GET /api/v1/projects` sem token continua a dar
**401**.

**CA-5 · Anti-drift** — o conjunto de authorities devolvido por `getRules()` é igual ao conjunto de
permissões da secção 4. A configuração e este documento não podem divergir.

**CA-6 · End-to-end** — depois de registar as 8 permissões no System Administration e atribuí-las a um
perfil: com `IGRP_AUTHORIZATION_SERVICE_ADAPTER=irn`, cookie `session_id=<sessionId>` e bearer do
Keycloak, o log DEBUG mostra `STUDIO_PROJECTS:visualizar` etc. nas authorities. Repetir com um perfil sem
a permissão → 403.

**CA-7 · Cache (RF-7)** — dois pedidos seguidos com o mesmo `session_id` produzem **um** só
`GET /Auth/me` no log do `IrnAuthClient`.

**CA-8 · Perfis de desenvolvimento** — confirmar que a aplicação arranca e autentica nos perfis
`development` e `staging`, dado o `@Profile("!development & !staging")` no bean `jwtDecoder` (5.4).

---

## 9. Fora do âmbito

- **Declarar o catálogo no iGRP Studio** (`.igrpstudio/project/permissions/*.json` e
  `.igrpstudio/shared/permissions/*.json`) — adiado por decisão (D-5). Para quando se retomar: o ecrã lê
  de `.igrpstudio/<módulo>/permissions/`, **não** do `permissions.json` da raiz (esse está em
  `IGNORED_PATHS`, `igrp-studio-ide/src/main/helpers/index.ts:119`), e o formato é
  `{"type":"permission","name":"STUDIO_PROJECTS:visualizar","description":"…","endpoints":["getProjects", …]}`,
  com `endpoints` = os `actionName` dos `controllers/*.json`. O `STUDIO_PARAMETERIZATION` iria para
  `shared/` — o `ParameterizationController` não é gerado e não tem JSON de controller. Os ficheiros têm
  de ser escritos à mão: o *Save* da UI rebenta.
- **Implementar `SpringEngine.createPermission()`** no `igrp-studio-ide` e a emissão de `@PreAuthorize`
  no `@igrp/igrp-studio-springboot-engine`. Fecharia o ciclo Studio→código, mas é noutro repo e noutro
  package npm.
- **Autorização por projeto (multi-tenancy).** As regras acima autorizam *operações*, não *objetos*: quem
  tiver `STUDIO_PROJECTS:visualizar` vê todos os projetos. O `appCode` do `ProjectEntity` seria o eixo
  natural para isso, mas está por terminar — `project/domain/models/Project.java:47` tem
  `// todo figure out how to handle appCode properly`. É desenho novo, não um guard.
- **CORS wildcard com `allowCredentials=true`** — `SecurityConfig` aceita qualquer origem com
  credenciais. É um P0 distinto, com spec própria.
- **Relatório ENISA — Advanced Ethical Hacking**: o PDF não tem camada de texto (páginas exportadas como
  vetor/imagem), por isso não foi incorporado. Findings que mapeiem para endpoints específicos entram no
  ajuste da configuração da secção 6.
