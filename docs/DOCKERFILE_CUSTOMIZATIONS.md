# IGRP Process Studio Backend Dockerfile

## 1. Ambiente Dev Zing

## 1.1 Base Images

Foram utilizadas as seguintes imagens base, de forma a ter acesso ao bash dentro dos containers, uma vez que um dos requisitos ao correto funcionamento
no ambiente DEV da IRN, a injecção de secrets atravez do agente de Hashicorp Vault, injecção essa que é feita atravez de comando bash:

maven:3.9.9-eclipse-temurin-23 AS build
eclipse-temurin:23-jre


## 1.2 Instalação de Certificados

Para instlação dos certificados internos da IRN, foram providenciados os seguintes certificados:

irn.internal.crt
justica-ca-root.crt
justica-sub-ca.crt

Se seguida, foi adicionada a seguinte secção no Dockerfile, de forma a serem instalados esses certificados:


```Dockerfile
COPY certs/irn/*.crt /usr/local/share/ca-certificates/

RUN apt-get update && apt-get install -y ca-certificates && \
    update-ca-certificates && \
    for cert in /usr/local/share/ca-certificates/*.crt; do \
      keytool -importcert -trustcacerts \
      -keystore "$JAVA_HOME/lib/security/cacerts" \
      -storepass changeit -noprompt \
      -alias "$(basename $cert .crt)" \
      -file "$cert"; \
    done && \
    rm -rf /var/lib/apt/lists/*
```

## 2. Ambiente Dev IRN

## 2.1 Base Images

No ambiente DEV da IRN, uma vez que são utilizadas imagens internas que incluem já a instalação dos devidos certificados, não é utilizada nenhuma secção de instalação de certificados no Dockerfile do mesmo, as imagens utilizadas são as seguintes:

FROM docker.tools.irn.internal/base/java-sdk:1.0.0 AS build
FROM docker.tools.irn.internal/base/java-jre:1.0.0


## 2.2 Preservação de Dockerfile nos ambientes da IRN

Importa referir que será sempre necessário ter um elevado grau de atenção ao efetuar Merge Requests de código que vem diretamente dos repositórios da Zing, para que prevaleça sempre o Dockerfile customizado da IRN


