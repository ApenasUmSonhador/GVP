# 👕 GVP - Gestor de Vestuário Pessoal

Sistema em Java Swing para gerenciar roupas, lavagens, empréstimos, looks e estatísticas pessoais.

## 🛠 Funcionalidades

- 📦 Cadastro e listagem de itens com imagens e categorias
- 🧼 Registro de lavagens por item
- 👗 Montagem de looks com peças próprias e emprestadas
- 🔄 Empréstimo de roupas entre usuários
- 📊 Estatísticas visuais (usos, lavagens, tipos, turnos)
- 🌙 Alternância entre tema claro e escuro
- 🔐 Sistema de login com persistência por usuário
- 📆 Registro de uso contextual de looks (ex: "aniversário da Maria")

## 💻 Tecnologias Utilizadas

- Java 17+
- Swing (interface gráfica)
- SQLite (persistência)
- JDBC (acesso a banco de dados)
- MVC (organização por camadas)

## 📸 Estrutura de Diretórios

```
GVP/
├── src/
│   ├── dao/           # Acesso a dados
│   ├── model/         # Modelos e enums
│   ├── util/          # Sessão, conexão e temas
│   └── view/          # Telas
├── img/               # Imagens dos itens
├── lib/               # .jar do SQLite
├── data/gvb.db        # Banco de dados SQLite
├── .gitignore
└── README.md
```

## ▶️ Como Executar

1. Clone o repositório:
   ```bash
   git clone https://github.com/ApenasUmSonhador/GVP.git
   cd GVP
   ```

2. Abra na sua IDE de preferência com suporte a projetos Java.

3. Compile o projeto via:
```bash
   javac -cp ".:lib/*" -d bin src/**/*.java    
```

4. Execute o projeto via:
```bash
   java -cp ".:lib/sqlite-jdbc-3.43.0.0.jar:src" Main    
```

## 📌 Requisitos

- Java 17+
- SQLite Driver JDBC incluído (não requer instalação externa)

## ✍️ Autor

Projeto acadêmico desenvolvido por **Arthur Nunes** para disciplina de Técnicas de Programação.

---

## 📄 Licença

Distribuído sob a licença MIT. Consulte o arquivo [LICENSE](LICENSE) para mais informações.