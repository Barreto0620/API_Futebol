# Quarkus Futebol CRUD

Este projeto é uma **API REST** desenvolvida com o framework **Quarkus** utilizando **Java 21**, com o objetivo de gerenciar dados relacionados ao universo do futebol. A aplicação oferece operações completas de **CRUD** para **Times**, **Partidas**, **Jogadores** e um recurso extra de **Destaques**, que identifica o jogador com maior número de gols em uma partida.

---

## 🚀 Tecnologias Utilizadas

- **Java 21** (Eclipse Adoptium)
- **Quarkus**
- **JPA (Jakarta Persistence)**
- **Hibernate ORM**
- **RESTEasy Reactive**
- **Swagger UI (OpenAPI)** – Documentação interativa
- **Banco de dados H2** (ambiente de desenvolvimento)
- **Jackson** – Serialização JSON

---

## 📦 Entidades

### 🟢 Time
- `id`
- `nome`
- `cidade`
- `estado`
- Relacionamento com jogadores e partidas

### 🟠 Partida
- `id`
- `dataHora`
- `local`
- `timeMandante`
- `timeVisitante`
- `placarMandante`
- `placarVisitante`

### 🔵 Jogador
- `id`
- `nome`
- `posicao`
- `numeroCamisa`
- Associação com Time
- Pode ser destaque em uma partida

### ⭐ Destaque
- `id`
- `partida` (OneToOne)
- `jogador` (ManyToOne)
- `golsMarcados`

---

## ✅ Funcionalidades

- CRUD completo:
  - `/times`
  - `/partidas`
  - `/jogadores`
  - `/destaques`
- Relacionamentos entre entidades com JPA
- Documentação automática via Swagger
- Separação de responsabilidades com `Entity`, `Repository` e `Resource`

---

## 🔁 Rotas

### Time (`/times`)
- `GET /times`
- `GET /times/{id}`
- `POST /times`
- `PUT /times/{id}`
- `DELETE /times/{id}`

### Partida (`/partidas`)
- `GET /partidas`
- `GET /partidas/{id}`
- `POST /partidas`
- `PUT /partidas/{id}`
- `DELETE /partidas/{id}`

### Jogador (`/jogadores`)
- `GET /jogadores`
- `GET /jogadores/{id}`
- `POST /jogadores`
- `PUT /jogadores/{id}`
- `DELETE /jogadores/{id}`

### Destaque (`/destaques`)
- `GET /destaques`
- `GET /destaques/{id}`
- `POST /destaques`
- `DELETE /destaques/{id}`

---

## 🧪 Como Executar o Projeto

1. Clone o repositório:
   ```bash
   git clone https://github.com/Barreto0620/Quarkus-Futebol-CRUD.git
   cd Quarkus-Futebol-CRUD
   ```

2. Execute o projeto em modo de desenvolvimento:
   ```bash
   ./mvnw quarkus:dev
   ```

3. Acesse a documentação da API:
   [http://localhost:8080/q/swagger-ui](http://localhost:8080/q/swagger-ui)

---

## 📂 Estrutura de Pacotes

- `org.senac.entity` → Entidades JPA
- `org.senac.repository` → Repositórios Panache
- `org.senac.resource` → Controladores REST

---

## 📌 Considerações Finais

Este projeto tem como objetivo apresentar uma API moderna e robusta utilizando **Quarkus**, com boas práticas de codificação, documentação e estrutura. Ideal para quem deseja entender como aplicar o Quarkus na construção de soluções RESTful profissionais.

---

## Código-fonte:
- Repositório no GitHub: [Link para o repositório]()

## Licença:
Este projeto está licenciado sob a Licença MIT - sinta-se à vontade para usar, modificar e distribuir de acordo com os termos da licença.

## Contribuições:
Contribuições e feedbacks são bem-vindos! Se você quiser contribuir para o projeto ou reportar problemas, fique à vontade para abrir uma issue ou enviar um pull request.

## Contato:
- LinkedIn: [www.linkedin.com/in/gabriel-barreto-oliveira](https://www.linkedin.com/in/gabriel-barreto-oliveira)
- E-mail: gabrielprozds@email.com

Obrigado por visitar meu repositório e por seu interesse no "Quarkus Futebol Crud"!
