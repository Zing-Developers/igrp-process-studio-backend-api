# `.igrpstudio` — modelo do gerador iGRP Studio

Este diretório é a **fonte de verdade do gerador**: uma regeneração reescreve os ficheiros marcados
`THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO` (DTOs em `application/dto`, entidades,
controllers). Um campo que só exista no Java é apagado na próxima regeneração.

## Regra

Sempre que se toca num DTO gerado, actualiza-se o JSON correspondente aqui — no mesmo commit.
Verificação: `python3 scripts/check_igrpstudio_drift.py` (falha com exit 1 se houver drift).

## Convenções deste repo

- DTO: `name` **sem** sufixo `DTO` (ex.: `ProjectResponse` → classe `ProjectResponseDTO`); o ficheiro
  leva o sufixo. Referências a outro DTO também sem sufixo: `"objectType":"dto","type":"UserProfile","module":"project"`.
- Datas: `"type":"datetime"` (gera `LocalDateTime`, serializado ISO sem zona).
- Trio de auditoria: `createdAt`/`updatedAt` + `createdBy`/`lastModifiedBy` +
  `userProfileCreatedBy`/`userProfileLastModifiedBy` (perfil resolvido pelo `AuditUserEnricher`).
  `lastModifiedBy` mantém-se por compatibilidade com o frontend; a management API usa `updatedBy`.

## Código feito à mão que agora está modelado (atenção ao regenerar)

| Ficheiro | Onde vive no código | Nota |
|---|---|---|
| `shared/models/M2mApiKeyEntity.json` | `shared/infrastructure/persistence/entity/M2mApiKeyEntity` | Sem `AuditEntity`/Envers **por design**. Implementação manual (hash HMAC, `active`, `expires_at`) — reconciliar, não substituir. |
| `shared/models/IAMUserProfileEntity.json` | `shared/infrastructure/persistence/entity/IAMUserProfileEntity` | Store de perfis sincronizado dos claims do JWT (`IAMUserProfileSyncFilter`); lean, sem audit. |
| `shared/controllers/M2mKeyController.json` | `shared/security/m2m/M2mKeyController` | Gate super-admin JWT-only está no `SecurityConfig`. |
| `shared/controllers/ParameterizationController.json` | `shared/interfaces/rest/ParameterizationController` | Resposta é `List<EnumItem<String>>` do framework — modelada como `object` lista. |
| `shared/dto/M2mKey*DTO.json` | `shared/application/dto/M2mKey*DTO` | Payloads das rotas `/m2m-keys`. |
| `project/dto/UserProfileDTO.json`, `ProjectSummaryDTO.json` | `project/application/dto` | Antes viviam como records fora da pasta gerada. |

Fora do gerador, sem modelo (infra de segurança): `SecurityConfig`, `IAMUserProfileSyncFilter`,
`AuditUserEnricher`, `AuditedResponse`, `AuditMapping`, `ApplicationAuditorAware`, `AuditTrail`, `ProjectRef`.
