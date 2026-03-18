
solução oara gerenciamento de assembleias e pautas de votação, desenvolvida em Java 21 com Spring Boot 3. O sistema utiliza o conceito de Server-Driven UI, onde o backend dita como o aplicativo mobile deve renderizar as telas de interação.

a. Pré-requisitos: JDK 21 e Maven instalados.

b. mvn clean install
mvn spring-boot:run

c. Configuração de IP: No arquivo src/main/resources/application.properties, altere app.api-host para o seu IP local se for testar em um celular.

d. Swagger UI: Acesse http://localhost:8080/swagger-ui.html para documentação interativa


Sistema de Votação Cooperativa - Backend API

solução oara gerenciamento de assembleias e pautas de votação, desenvolvida em Java 21 com Spring Boot 3. O sistema utiliza o conceito de Server-Driven UI, onde o backend dita como o aplicativo mobile deve renderizar as telas de interação.

Como Executar

Pré-requisitos

Java 21 (essencial para Virtual Threads/Performance)

Maven 3.9+

Passos para rodar localmente
Clone o repositório: git clone <url-do-repo>

Compile o projeto: mvn clean install

Inicie a aplicação: mvn spring-boot:run

Acesse a Documentação: http://localhost:8080/swagger-ui.html

Obs: Os dados são persistidos automaticamente em um banco de dados H2 local no diretório ./data/db. O restart da aplicação não apaga os votos.

Arquitetura
1. Server-Driven UI (Foco Mobile)
Diferente de APIs REST tradicionais que retornam apenas dados, esta API possui endpoints de /telas. Isso permite que o aplicativo mobile mude sua interface  (adicionar campos, mudar botões) sem  precisar de um novo deploy nas lojas (App  Store/Play Store).
2. Performance (Tarefa Bônus 2)
Para suportar centenas de milhares de votos simultâneos:
  Virtual Threads: Habilitadas via spring.threads.virtual.enabled=true. Elas permitem que o servidor processe milhares de requisições de I/O (espera de banco/rede) sem esgotar a memória.
  Agregação via SQL: A contagem de votos é feita via SELECT COUNT no banco de dados, evitando o carregamento de objetos pesados para a memória da JVM.
3. Integração com CPF (Tarefa Bônus 1)
Implementamos um CpfValidatorClient que simula um serviço externo:
Retorna 404 Not Found para CPFs inválidos ou usuários não autorizados (UNABLE_TO_VOTE).
A lógica é desacoplada, facilitando a troca por uma API real (ex: Serasa ou Receita) no futuro.
4. Versionamento (Tarefa Bônus 3)
Utilizamos Versionamento por URI (/api/v1/).

Endpoints Principais
POSR /api/v1/pautas Cadastra uma nova pauta.

POST /api/v1/pautas/{id}/abrir Abre a sessão (Default: 1 min).

POST /api/v1/pautas/{id}/votos Registra o voto (Valida CPF e tempo).

GET /api/v1/pautas/{id}/resultado Retorna o placar final.

GET /api/v1/telas/pautas/votar Retorna o json de selecao

Docker (Nuvem)
Para rodar em container:

bash
docker build -t votacao-cooperativa .
docker run -p 8080:8080 -v $(pwd)/data:/data votacao-cooperativa



Testes Automatizados:

Impedimento de voto duplo.

Validação de tempo de sessão expirado.

Cálculo de resultado (Sim vs Não).