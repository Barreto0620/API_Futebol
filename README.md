# ⚽ Quarkus Futebol CRUD

API RESTful desenvolvida com **Java 21** e **Quarkus**, voltada à gestão de dados do universo do futebol. A aplicação oferece operações completas de **CRUD** para **Times**, **Partidas**, **Jogadores** e **Destaques** (jogador com maior número de gols em uma partida).

---

## 📌 Descrição do Projeto

Este projeto tem como objetivo demonstrar uma aplicação de arquitetura RESTful utilizando o **framework Quarkus**, respeitando os princípios de design de APIs modernas, com organização clara das camadas e documentação interativa.

---

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Quarkus**
- **JPA (Jakarta Persistence)**
- **Hibernate ORM**
- **RESTEasy Reactive**
- **Swagger UI (OpenAPI)**
- **Banco de dados H2 (in-memory)**
- **Maven Wrapper**
- **Thunder Client (testes de API via VS Code)**

---

## 🧱 Entidades

### 🟢 Time
```json
{
  "id": 1,
  "nome": "Palmeiras",
  "cidade": "São Paulo",
  "estado": "SP"
}
```

### 🟠 Partida
```json
{
  "id": 1,
  "dataHora": "2024-05-12T16:00:00",
  "local": "Allianz Parque",
  "timeMandante": 1,
  "timeVisitante": 2,
  "placarMandante": 2,
  "placarVisitante": 1
}
```

### 🔵 Jogador
```json
{
  "id": 1,
  "nome": "Gabriel Jesus",
  "posicao": "Atacante",
  "numeroCamisa": 9,
  "timeId": 1
}
```

### ⭐ Destaque
```json
{
  "id": 1,
  "partidaId": 1,
  "jogadorId": 1,
  "golsMarcados": 2
}
```

---

## 🔁 Endpoints da API

### `/times`
- `GET /times` – Listar todos os times  
- `GET /times/{id}` – Obter time por ID  
- `POST /times` – Criar novo time  
- `PUT /times/{id}` – Atualizar time  
- `DELETE /times/{id}` – Remover time  

### `/partidas`
- `GET /partidas`  
- `GET /partidas/{id}`  
- `POST /partidas`  
- `PUT /partidas/{id}`  
- `DELETE /partidas/{id}`  

### `/jogadores`
- `GET /jogadores`  
- `GET /jogadores/{id}`  
- `POST /jogadores`  
- `PUT /jogadores/{id}`  
- `DELETE /jogadores/{id}`  

### `/destaques`
- `GET /destaques`  
- `GET /destaques/{id}`  
- `POST /destaques`  
- `DELETE /destaques/{id}`  

---

## 🧪 Como Executar o Projeto

1. Clone o repositório:
```bash
git clone https://github.com/Barreto0620/API_Futebol.git
cd API_Futebol
```

2. Execute a aplicação:
```bash
./mvnw quarkus:dev
```

3. Acesse a documentação Swagger:
[http://localhost:8080/q/swagger-ui](http://localhost:8080/q/swagger-ui)

---

## 📁 Estrutura de Pacotes

```
src/main/java/org/senac/
│
├── entity        # Entidades JPA
├── repository    # Acesso ao banco de dados (Panache)
└── resource      # Endpoints REST
```

---

## 🧪 Exemplos no Thunder Client (VS Code)

Crie as requisições manualmente no Thunder Client com os seguintes dados:

### 📌 Criar um Time

- **Método:** `POST`  
- **URL:** `http://localhost:8080/times`  
- **Body (JSON):**
```json
{
  "nome": "Palmeiras",
  "cidade": "São Paulo",
  "estado": "SP"
}
```

---

### 📌 Criar um Jogador

- **Método:** `POST`  
- **URL:** `http://localhost:8080/jogadores`  
- **Body (JSON):**
```json
{
  "nome": "Gabriel Menino",
  "posicao": "Meio-Campo",
  "numeroCamisa": 25,
  "timeId": 1
}
```

---

### 📌 Criar uma Partida

- **Método:** `POST`  
- **URL:** `http://localhost:8080/partidas`  
- **Body (JSON):**
```json
{
  "dataHora": "2024-05-15T18:30:00",
  "local": "Morumbi",
  "timeMandante": 1,
  "timeVisitante": 2,
  "placarMandante": 3,
  "placarVisitante": 2
}
```

---

### 📌 Criar um Destaque

- **Método:** `POST`  
- **URL:** `http://localhost:8080/destaques`  
- **Body (JSON):**
```json
{
  "partidaId": 1,
  "jogadorId": 1,
  "golsMarcados": 2
}
```

---

## Documentações Adicionais:

- 📜 [Código de Conduta](codigo_conduta.md)
- ⚖️ [Modelo de Governança](modelo_governanca.md)

## Licença:
Este projeto está licenciado sob a Licença MIT - sinta-se à vontade para usar, modificar e distribuir de acordo com os termos da licença.

## Contribuições:
Contribuições e feedbacks são bem-vindos! Se você quiser contribuir para o projeto ou reportar problemas, fique à vontade para abrir uma issue ou enviar um pull request.

## Contato:
- LinkedIn: [www.linkedin.com/in/gabriel-barreto-oliveira](https://www.linkedin.com/in/gabriel-barreto-oliveira)
- E-mail: gabrielprozds@email.com

---

Obrigado por visitar meu repositório e por seu interesse no "Quarkus Futebol Crud"!
