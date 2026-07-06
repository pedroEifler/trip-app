# ✈️ Trip — Gerenciador de Viagens

Aplicativo Android para **gerenciamento de viagens pessoais**, com geração de **roteiros turísticos por Inteligência Artificial**. Desenvolvido com **Jetpack Compose** e arquitetura moderna Android (MVVM).

---

## 📋 Resumo do Projeto

> 🎓 **Projeto acadêmico** desenvolvido na disciplina de **Desenvolvimento Mobile (Android)**, com o objetivo de aplicar, na prática, conceitos de UI declarativa, arquitetura MVVM, persistência local, integração com serviços de localização e consumo de uma API de IA generativa.

### O que é
O **Trip** é um app Android nativo que ajuda o usuário a **organizar e acompanhar suas viagens**. Ele reúne, em um só lugar, o cadastro das viagens, a detecção automática da viagem que está em andamento (pela localização atual), a galeria de fotos de cada viagem e a geração de um roteiro turístico personalizado com **Inteligência Artificial**.

### O que faz
- 🔐 **Autenticação local** — cadastro de usuário, login e redefinição de senha (persistidos com Room).
- 🧭 **Menu lateral (Drawer)** — acesso rápido a *Nova Viagem*, *Minhas Viagens* e *Sobre*.
- 🧳 **Gestão de viagens (CRUD)** — criar, listar, **editar** (long press) e **excluir** (swipe), com tipos *Lazer* 🏖️ e *Negócios* 💼 e viagens de exemplo pré-cadastradas via *migration*.
- 📍 **Viagem corrente** — detecta automaticamente a viagem em andamento cruzando a **cidade atual** (geolocalização) com as datas da viagem, exibindo orçamento e **total de gastos**.
- 🗺️ **Localização no mapa** — abre a localização da viagem no app do **Google Maps** por meio de um *Intent* (`geo:`), sem exigir chave de API.
- 📷 **Fotos da viagem** — galeria por viagem com adição via **Câmera** (FileProvider) ou **Galeria** (Photo Picker) e remoção via long press.
- 🤖 **Roteiro com IA** — a partir do destino, tipo, período e interesses, gera um **roteiro dia a dia** personalizado usando o modelo **Google Gemini**, com renderização em Markdown e tratamento de erros/tentativas.

### Tecnologias utilizadas

| Categoria | Tecnologia | Versão |
|---|---|---|
| Linguagem | Kotlin | 2.2.10 |
| UI | Jetpack Compose (BOM) + Material 3 | 2024.09.00 |
| Navegação | Navigation 3 (Runtime + UI) | 1.0.1 |
| Arquitetura | MVVM + Lifecycle ViewModel | 2.10.0 |
| Banco de dados | Room (Runtime + KTX) + KSP | 2.8.4 |
| IA generativa | **Google Gemini API** (`gemini-2.5-flash`) | — |
| Rede | Retrofit + OkHttp Logging + kotlinx.serialization | 2.11.0 / 4.12.0 / 1.9.0 |
| Localização | Play Services Location | 21.3.0 |
| Mapas | Maps Compose + Play Services Maps | 4.4.1 / 18.2.0 |
| Imagens | Coil Compose | 2.7.0 |
| Permissões | Accompanist Permissions | 0.37.3 |
| Ícones | Material Icons Extended | 1.6.0 |
| Build | Android Gradle Plugin / Gradle | 9.1.0 / 9.3.1 |

**Arquitetura em camadas (MVVM):**

```
UI (Compose Screens) ⇄ ViewModel (estado + lógica) ⇄ Repository ⇄ DAO/Room (local) · Retrofit (Gemini API)
```

---

## 🖼️ Imagens do Projeto

<img width="240" height="537" alt="Login" src="https://github.com/user-attachments/assets/865c96cf-5bf7-46f1-bf9c-a3a0eccc2a12" />

<img width="240" height="537" alt="Home" src="https://github.com/user-attachments/assets/e43d6e41-b024-41a9-9c91-1344cf0cb849" />

<img width="240" height="537" alt="Roteiro" src="https://github.com/user-attachments/assets/de24d3ed-4a80-4404-baa8-8c7d843f2444" />

---

## 🚀 Como Executar o Projeto Localmente

### ✅ Pré-requisitos
- **Android Studio** Ladybug (ou superior)
- **JDK 11**
- **Android SDK** — mínimo API 24 (Android 7.0), target API 36
- **Gradle 9.3.1** (já incluso via *wrapper*, não precisa instalar)
- Uma **chave da API do Google Gemini** (gratuita) para a funcionalidade *Roteiro com IA*

### 1. Clonar o repositório
```bash
git clone https://github.com/seu-usuario/trip.git
cd trip
```

### 2. Configurar o `local.properties`
Na raiz do projeto, edite (ou crie) o arquivo **`local.properties`** com o caminho do SDK e a chave da API do Gemini:

```properties
# Caminho do Android SDK (normalmente já preenchido pelo Android Studio)
sdk.dir=C\:\\Users\\SeuUsuario\\AppData\\Local\\Android\\Sdk

# Chave da API do Google Gemini (obrigatória para o "Roteiro com IA")
GEMINI_API_KEY=coloque_sua_chave_aqui

# (Opcional) Chave do Google Maps — não é necessária para o mapa atual,
# que abre o app do Google Maps via Intent geo:
# MAPS_API_KEY=coloque_sua_chave_do_maps_aqui
```

> 🔑 Gere sua chave gratuita do Gemini no **[Google AI Studio](https://aistudio.google.com/app/apikey)**.
>
> ⚠️ **Segurança:** o arquivo `local.properties` **não deve ser versionado** (mantenha-o no `.gitignore`). Nunca faça commit da sua chave de API.

### 3. Sincronizar e executar
1. Abra o projeto no **Android Studio** e aguarde a **sincronização do Gradle**.
2. Selecione um **emulador** ou **dispositivo físico** (API 24+).
3. Clique em **Run ▶️**.

Ou, via terminal:
```bash
./gradlew installDebug   # instala no dispositivo/emulador conectado
# ou
./gradlew assembleDebug  # gera o APK de debug em app/build/outputs/apk/debug/
```

### 📝 Observações
- **Permissão de localização:** conceda a permissão quando solicitada — ela é usada para detectar a **cidade atual** e identificar a **viagem corrente**.
- **Roteiro com IA:** requer conexão com a internet e uma `GEMINI_API_KEY` válida configurada no `local.properties`.
- **Mapa:** abre no app do **Google Maps** por meio de um *Intent* (`geo:`), portanto **não exige chave de API**.
- **Dados de exemplo:** ao iniciar, a *migration* cadastra 3 viagens de exemplo (Paris 🏖️, Nova York 💼 e Florianópolis 🏖️) vinculadas ao primeiro usuário.

