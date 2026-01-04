# ✈️ Trip Planner – NLW Journey

Este projeto é uma **API de planejamento de viagens**, desenvolvida na reprise do evento **NLW 16 – Journey Java** da Rocketseat.

A aplicação permite que usuários:

* Organizem viagens
* Convidem participantes por e-mail - feature não implementada
* Agendem atividades
* Gerenciem links importantes relacionados à jornada

---

## 🖼️ Preview da Aplicação 
obs: Essa preview no figma, disponível [nesse link](https://www.figma.com/design/dLzTplAYO4ThMKluIHIlXr/NLW-Journey-%E2%80%A2-Planejador-de-viagem--Community-?node-id=3-376&p=f&t=cxLxK3oa2USeA7JO-0) foi entregue pronto pelo time da Rocketseat.

<p align="center">
  <img src="https://github.com/user-attachments/assets/ae1c996f-1868-437b-b3bc-541fe094a049" width="90%" alt="Viagem"/>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/9c13648a-7635-4190-a3ae-0ff9bf47c1a1" width="45%" alt="Atividades"/>
  <img src="https://github.com/user-attachments/assets/2e75c6df-53ac-4e44-84f3-206a4fc5fba5" width="45%" alt="Links"/>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/55f17c0a-9672-4031-b28e-47fede99221f" width="30%" alt="Convite"/>
</p>


---

## 🚀 Tecnologias Utilizadas

* **Java 21**
* **Spring Boot 4.0.1**
* **Spring Data JPA**
* **H2 Database** (banco de dados em memória)
* **Flyway** (migração de banco de dados)
* **Lombok**

---

## 🛠️ Refatorações e Boas Práticas

Diferente da versão inicial apresentada no curso, refatorei esse projeto com foco em **Clean Code** e **Arquitetura em Camadas**.

### Destaques:

* **Camada de Service isolada** -
  Toda a lógica de negócio foi movida dos Controllers para Services específicos, garantindo melhor separação de responsabilidades.

* **Tratamento Global de Erros** -
  Implementação de um `CustomGlobalExceptionHandler` utilizando `@RestControllerAdvice`, retornando respostas padronizadas via `ExceptionResponse`.

* **Exceções Personalizadas**

  * `ResourceNotFoundException`: lançada quando IDs de viagens ou participantes não são encontrados.
  * `InvalidDateException`: garante que:

    * atividades não sejam marcadas fora do período da viagem;
    * a viagem não possua datas inconsistentes.

* **Status HTTP Semânticos** -
  Uso adequado dos códigos HTTP, por exemplo:

  * `201 Created` para criação de recursos
  * `200 OK` para consultas e atualizações

---

## 📋 Endpoints da API

### ✈️ Viagens (Trips)

| Método | Endpoint                  | Descrição                                          |
| ------ | ------------------------- | -------------------------------------------------- |
| POST   | `/api/trips`              | Cria uma nova viagem                               |
| GET    | `/api/trips/{id}`         | Busca os detalhes de uma viagem                    |
| PUT    | `/api/trips/{id}`         | Atualiza os dados de uma viagem                    |
| POST   | `/api/trips/{id}/confirm` | Confirma a viagem e dispara e-mails aos convidados |


### 🗓️ Atividades (Activities)

| Método | Endpoint                     | Descrição                                          |
| ------ | ---------------------------- | -------------------------------------------------- |
| POST   | `/api/trips/{id}/activities` | Registra uma atividade dentro do período da viagem |
| GET    | `/api/trips/{id}/activities` | Lista todas as atividades da viagem                |


### 👥 Participantes (Participants)

| Método | Endpoint                         | Descrição                               |
| ------ | -------------------------------- | --------------------------------------- |
| GET    | `/api/trips/{id}/participants`   | Lista os convidados da viagem           |
| POST   | `/api/trips/{id}/invite`         | Convida um novo participante por e-mail |
| POST   | `/api/participants/{id}/confirm` | Confirma a presença de um participante  |


### 🔗 Links 

| Método | Endpoint                | Descrição                      |
| ------ | ----------------------- | ------------------------------ |
| POST   | `/api/trips/{id}/links` | Adiciona um link à viagem      |
| GET    | `/api/trips/{id}/links` | Lista todos os links da viagem |

---

## 🏗️ Como Executar o Projeto

### Pré-requisitos

* **Java 21** instalado

### Passos

1. Clone o repositório:

```bash
git clone https://github.com/Leturnos/rocketseat-spring-trip-planner.git
```

2. Execute a aplicação utilizando o Maven Wrapper:

```bash
./mvnw spring-boot:run
```

---

## 🗄️ Console do H2

O banco de dados em memória pode ser acessado em:

* **URL:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
* **JDBC URL:** `jdbc:h2:mem:planner`

---

## ⚖️ Licença

Esse projeto está sob a Licença MIT 

---

📌 *Projeto desenvolvido por Leandro seguindo as aulas da reprise do NLW 16 - journey Java da **Rocketseat** e evoluindo além do conteúdo base*
