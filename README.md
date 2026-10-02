# LabControl Web — Etapa 9

Aplicação web completa do LabControl, desenvolvida na **Etapa 9**
(etapa final) do Projeto Integrador (curso Técnico em Desenvolvimento
de Sistemas — Senac), integrando:

- A lógica de negócio das **Etapas 6/7** (services, regras de
  validação, cálculo de nível de estoque), agora persistida via
  **Spring Data JPA + MySQL** em vez do JDBC manual usado no desktop.
- O front-end da **Etapa 8** (HTML/CSS/JS), portado para
  **Thymeleaf**, com o JavaScript adaptado para consumir uma API
  REST real em vez de dados simulados em `sessionStorage`.
- Os testes JUnit da **Etapa 7**, migrados para usar **Mockito** no
  lugar dos DAOs fake manuais (a persistência agora é Spring Data
  JPA, então o "dublê de teste" passa a ser um repository mockado).

## Stack

- Java 17, Spring Boot 3.3.4 (Spring MVC + Spring Data JPA + Thymeleaf)
- MySQL (via `mysql-connector-j`)
- Empacotamento: **WAR** para Tomcat externo (mesmo padrão usado no
  projeto Casa Cultural Cinema — `spring-boot-starter-tomcat` em
  `provided`, `LabControlApplication extends SpringBootServletInitializer`)
- Testes: JUnit 5 + Mockito

## Como rodar

### 1. Banco de dados

Rode o script `UC_15_Etapa9.sql` em um MySQL local (cria o banco
`labcontrol`, as tabelas e alguns dados de exemplo). **Leia o
comentário no topo do script** se for reaproveitar um banco já criado
pela Etapa 6 — os valores de enum mudaram de formato.

Ajuste usuário/senha do MySQL em
`src/main/resources/application.properties` se necessário (padrão:
`root`/`root`).

### 2. Rodar localmente (desenvolvimento)

No NetBeans, com o projeto Maven importado:

```
mvn spring-boot:run
```

Acesse `http://localhost:8080/login`.

### 3. Deploy como WAR (Tomcat externo)

```
mvn clean package
```

Isso gera `target/labcontrol.war` — um WAR tradicional, pronto para
qualquer Tomcat 10.x externo. Copie esse arquivo para a pasta
`webapps` do seu Tomcat, ou registre o Tomcat em Tools > Servers no
NetBeans, selecione-o em Properties > Run do projeto, e clique em
Run (F6) — o deploy é feito automaticamente. A aplicação fica
disponível em `http://localhost:8080/labcontrol/login` (o contexto
`/labcontrol` vem do `<finalName>` definido no pom.xml).

> **Nota técnica**: o `spring-boot-maven-plugin` está configurado com
> `skip=true` no `pom.xml`. Sem isso, o goal `repackage` (que roda
> por padrão sempre que o plugin está presente) embutiria um Tomcat
> inteiro dentro do WAR (formato `BOOT-INF/`), que só o próprio
> Spring Boot sabe iniciar — um Tomcat *externo* não reconhece esse
> formato como uma webapp válida, e o deploy simplesmente falha
> silenciosamente (o Tomcat sobe normalmente, mas a aplicação nunca
> aparece implantada). Com `skip=true`, `mvn package` gera o WAR
> "fino" tradicional que qualquer servlet container consegue
> implantar.

### 4. Rodar os testes

```
mvn test
```

Cobre as mesmas regras testadas na Etapa 7 (28 testes: cálculo de
nível de estoque, validações de cadastro, autenticação, autorização
por perfil), agora usando Mockito para simular os repositories.

**Usuários de teste** (login / senha), criados pelo script SQL:
- `admin` / `123456` — Administrador
- `tecnico` / `123456` — Técnico de Laboratório
- `operador` / `123456` — Operador

## Estrutura

```
src/main/java/br/com/labcontrol/
├── LabControlApplication.java   (classe principal, WAR-ready)
├── model/          (entidades JPA: Usuario, Equipamento + enums)
├── repository/     (Spring Data JPA — substitui os DAOs da Etapa 6)
├── service/        (regras de negócio — migradas da Etapa 6/7)
├── exception/      (exceções de domínio — migradas sem alterações)
├── dto/            (objetos de request/response da API REST)
├── controller/     (PaginaController + *ApiController + GlobalExceptionHandler)
└── config/         (SessaoInterceptor + WebConfig)

src/main/resources/
├── application.properties
├── templates/       (6 páginas Thymeleaf, portadas da Etapa 8)
└── static/
    ├── css/         (copiado sem alterações da Etapa 8)
    └── js/          (api.js novo + os demais adaptados para fetch())

src/test/java/br/com/labcontrol/service/
├── EquipamentoServiceTest.java  (16 testes, Mockito)
├── UsuarioServiceTest.java      (6 testes, Mockito)
└── AutorizacaoServiceTest.java  (6 testes, sem mocks — regra pura)
```

## O que mudou em relação à Etapa 8 (arquitetura)

- **Autenticação real**: sessão HTTP (`HttpSession`) criada no
  login, validada em toda chamada de API por `SessaoInterceptor` —
  não é mais possível contornar o login manipulando o JavaScript do
  navegador, como acontecia com o `sessionStorage` da Etapa 8.
- **Nível de estoque calculado no servidor**: o front-end não
  recalcula mais a regra em JavaScript; cada equipamento retornado
  pela API já vem com o campo `nivelEstoque` pronto (ver
  `EquipamentoDTO`), e a prévia dinâmica do formulário consulta
  `GET /api/equipamentos/nivel-estoque?quantidade=N`.
- **Senha nunca é exposta**: `UsuarioDTO.deEntidade()` propositalmente
  não copia o campo `senha` nas respostas da API.
- **Novo status de equipamento**: `MANUTENCAO` foi adicionado ao
  enum `StatusEquipamento` — o formulário da Etapa 8 já previa essa
  opção no HTML, mas o back-end desktop (Etapa 6) só tinha
  `DISPONIVEL`/`EM_USO`.

## Limitação conhecida deste pacote

Este projeto foi desenvolvido em um ambiente sem acesso ao Maven
Central e sem um JDK completo instalado, então **não foi possível
compilar nem rodar `mvn test` automaticamente** antes da entrega. Todo
o código foi revisado manualmente (balanceamento de chaves/parênteses,
consistência de pacotes, assinaturas de métodos entre repositories,
services e testes, imports). Ainda assim, **é essencial rodar `mvn
clean install` e `mvn test` no NetBeans antes de considerar a etapa
concluída** — qualquer erro de compilação real só aparecerá nesse
momento, e deve ser corrigido antes da entrega final.
