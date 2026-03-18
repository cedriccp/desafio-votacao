​Cadastrar Pauta: 
GET /api/v1/telas/pautas/nova -> O App renderiza o form e envia o POST para /pautas.

​Abrir Sessão: POST /api/v1/pautas/{id}/abrir enviando {"minutos": 5}.

​Votar: GET /api/v1/telas/pautas/{id}/votar -> O App renderiza os botões Sim/Não e envia o POST com o CPF para /votos.

​Resultado: GET /api/v1/pautas/{id}/resultado para ver quem ganhou.