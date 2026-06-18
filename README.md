# ✈️ Trip — Gerenciador de Viagens

Aplicativo Android para gerenciamento de viagens pessoais, desenvolvido com **Jetpack Compose** e arquitetura moderna Android.

---

## 📱 Telas

| Tela | Descrição |
|---|---|
| **Login** | Autenticação com e-mail e senha |
| **Cadastro** | Criação de nova conta de usuário |
| **Esqueci a senha** | Redefinição de senha por e-mail |
| **Home** | Menu principal com DrawerMenu lateral, mapa da viagem corrente e Bottom Bar |
| **Nova Viagem** | Formulário para cadastrar uma viagem |
| **Minhas Viagens** | Lista de viagens do usuário logado |
| **Editar Viagem** | Formulário pré-preenchido para edição |
| **Fotos** | Galeria de fotos da viagem corrente (câmera/galeria) |
| **Roteiro** | Placeholder (será implementado em outra tarefa) |
| **Sobre** | Informações do aplicativo |

---

## 🗂️ Funcionalidades

### Autenticação
- Cadastro de usuário (nome, e-mail, telefone, senha)
- Login com e-mail e senha
- Redefinição de senha
- Encerramento do app ao pressionar voltar na tela Home

### Menu Lateral (DrawerMenu)
- ✈️ Nova Viagem
- 🧳 Minhas Viagens
- ➖ Separador
- ℹ️ Sobre

### Viagens
- **Cadastrar** nova viagem com todos os campos obrigatórios
- **Listar** viagens do usuário logado em cards
- **Editar** via long press no card
- **Excluir** via swipe para a esquerda
- Ícone diferenciado por tipo: 🏖️ Lazer / 💼 Negócios
- Dados pré-cadastrados inseridos via migration

### Viagem Corrente (Home)
- Detecção automática da viagem em andamento pela cidade atual (geolocalização) e datas
- 🗺️ **Mapa** (Google Maps) com a localização atual da viagem
- **Bottom Bar** com as opções **Roteiro** e **Fotos**
  - **Roteiro** — placeholder (implementação em outra tarefa)
  - **Fotos** — habilitada apenas quando há viagem corrente

### Fotos da Viagem
- Galeria de todas as fotos vinculadas à viagem corrente (grade)
- Adicionar foto pela **Câmera** (FileProvider) ou pela **Galeria** (Photo Picker)
- Fotos copiadas para o armazenamento interno e vinculadas à viagem no banco
- Remover foto via long press

---

## 🧩 Estrutura do Projeto

```
app/src/main/java/com/example/trip/
│
├── data/
│   ├── local/
│   │   ├── entity/
│   │   │   ├── UserEntity.kt       # Entidade de usuário
│   │   │   └── TripEntity.kt       # Entidade de viagem + enum TripType
│   │   ├── dao/
│   │   │   ├── UserDao.kt          # DAO de usuário
│   │   │   └── TripDao.kt          # DAO de viagem (insert, update, delete, findById)
│   │   ├── AppDatabase.kt          # Room Database (v2 + migration)
│   │   └── TripTypeConverter.kt    # TypeConverter para TripType enum
│   └── repository/
│       ├── UserRepository.kt       # Lógica de usuário (login, register, resetPassword)
│       └── TripRepository.kt       # Lógica de viagem (save, update, delete, getById)
│
├── ui/
│   ├── components/
│   │   ├── DrawerMenu.kt           # Menu lateral (ModalNavigationDrawer)
│   │   ├── EmailField.kt
│   │   ├── InputField.kt
│   │   ├── LoginButton.kt
│   │   └── PasswordField.kt
│   ├── theme/
│   ├── LoginScreen.kt
│   ├── RegisterScreen.kt
│   ├── ForgotPasswordScreen.kt
│   ├── HomeScreen.kt               # Tela principal com DrawerMenu
│   ├── NewTripScreen.kt            # Formulário de nova viagem / edição
│   ├── MyTripsScreen.kt            # Lista de viagens com swipe e long press
│   └── AboutScreen.kt
│
├── viewmodel/
│   ├── LoginViewModel.kt
│   ├── RegisterViewModel.kt
│   ├── ForgotPasswordViewModel.kt
│   ├── NewTripViewModel.kt         # Suporta modo criação e edição
│   └── MyTripsViewModel.kt         # Coleta Flow de viagens do usuário
│
├── util/
│   └── EmailValidator.kt
│
├── MainActivity.kt                 # NavKeys + AppNavigation
└── TripApplication.kt             # Application com repositórios lazy
```

---

## 🗄️ Banco de Dados

### Tabelas

#### `users`
| Campo | Tipo | Descrição |
|---|---|---|
| `id` | Long (PK) | Auto gerado |
| `name` | String | Nome do usuário |
| `email` | String (unique) | E-mail |
| `phone` | String | Telefone |
| `password` | String | Senha |

#### `trips`
| Campo | Tipo | Descrição |
|---|---|---|
| `id` | Long (PK) | Auto gerado |
| `destination` | String | Destino da viagem |
| `type` | TripType | `LAZER` ou `NEGOCIOS` |
| `startDate` | Long | Data de início (epoch ms) |
| `endDate` | Long | Data de fim (epoch ms) |
| `budget` | Double | Orçamento (R$) |
| `description` | String | Descrição |
| `userId` | Long | FK para o usuário |

#### `photos`
| Campo | Tipo | Descrição |
|---|---|---|
| `id` | Long (PK) | Auto gerado |
| `tripId` | Long | FK para a viagem (ON DELETE CASCADE) |
| `filePath` | String | Caminho do arquivo no armazenamento interno |
| `createdAt` | Long | Data de criação (epoch ms) |

### Migrations
| Versão | Mudança |
|---|---|
| `v1 → v2` | Criação da tabela `trips` + 3 registros pré-cadastrados |
| `v2 → v3` | Criação da tabela `photos` (FK para `trips`) |

---

## 🏗️ Arquitetura

O projeto segue o padrão **MVVM** com as camadas:

```
UI (Compose Screens)
       ↕
ViewModel (estado + lógica de negócio)
       ↕
Repository (abstração de dados)
       ↕
DAO / Room (banco local SQLite)
```

### Navegação
Utiliza **Navigation3** (androidx.navigation3) com `NavKey` serializáveis:

```
Login → Register
      → ForgotPassword
      → Home → NewTrip(email)
             → MyTrips(email) → EditTrip(email, tripId)
             → About
             → Photos(email, tripId, destination)
             → Roteiro(email)
```

---

## 🛠️ Tecnologias e Bibliotecas

| Biblioteca | Versão | Uso |
|---|---|---|
| Kotlin | 2.2.10 | Linguagem principal |
| Jetpack Compose BOM | 2024.09.00 | UI declarativa |
| Material3 | — | Design System |
| Navigation3 Runtime/UI | 1.0.1 | Navegação entre telas |
| Room Runtime + KTX | 2.8.4 | Banco de dados local |
| KSP | — | Processamento de anotações Room |
| kotlinx.serialization | 1.9.0 | Serialização dos NavKeys |
| Lifecycle ViewModel | 2.10.0 | ViewModels + coroutines |
| Material Icons Extended | 1.6.0 | Ícones Material |
| Maps Compose | 4.4.1 | Mapa da viagem corrente |
| Play Services Maps | 18.2.0 | SDK do Google Maps |
| Play Services Location | 21.3.0 | Localização (cidade atual) |
| Coil Compose | 2.7.0 | Carregamento de imagens (fotos) |

---

## ⚙️ Requisitos

- **Android Studio** Ladybug ou superior
- **JDK 11**
- **Android SDK** mínimo: API 24 (Android 7.0)
- **Target SDK**: API 36

---

## 🚀 Como Executar

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/trip.git
   ```

2. Abra no **Android Studio**

3. Configure a **chave da API do Google Maps** no arquivo `local.properties`
   (necessária para exibir o mapa da viagem corrente):
   ```properties
   MAPS_API_KEY=suachaveaqui
   ```
   > Crie a chave no [Google Cloud Console](https://console.cloud.google.com/)
   > habilitando o serviço **Maps SDK for Android**. A chave é injetada no
   > `AndroidManifest.xml` via `manifestPlaceholders` durante o build.

4. Aguarde a sincronização do Gradle

5. Execute em um emulador ou dispositivo físico (API 24+)

---

## 📂 Dados Pré-Cadastrados

A migration `v1 → v2` insere automaticamente 3 viagens de exemplo vinculadas ao `userId = 1`:

| Destino | Tipo | Período | Orçamento |
|---|---|---|---|
| Paris, França | Lazer 🏖️ | Jan/2026 | R$ 8.500,00 |
| Nova York, EUA | Negócios 💼 | Fev/2026 | R$ 15.000,00 |
| Florianópolis, Brasil | Lazer 🏖️ | Mar/2026 | R$ 3.200,00 |

---

## 📋 Validações do Formulário de Viagem

Todos os campos são obrigatórios:

- **Destino** — não pode ser vazio
- **Tipo** — seleção obrigatória via RadioButton (Lazer / Negócios)
- **Data de Início** — seleção via DatePicker
- **Data de Fim** — seleção via DatePicker, deve ser após a data de início
- **Orçamento** — valor numérico positivo
- **Descrição** — não pode ser vazia

---

## 🔑 Funcionalidades de UX

- **Swipe para esquerda** no card → exclui a viagem (com fundo vermelho)
- **Long press** no card → abre tela de edição com dados pré-preenchidos
- **Botão voltar** na tela Home → encerra o aplicativo
- **Estado vazio** na lista de viagens → mensagem amigável
- **Indicador de carregamento** ao salvar/editar

