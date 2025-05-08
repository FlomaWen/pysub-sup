# ChatRouter

Un petit chat en temps réel basé sur Spring Boot, Spring Cloud GCP Pub/Sub et WebSocket (STOMP + SockJS).

---

## 📦 Architecture
[Browser Client] ↔ (STOMP/SockJS) ↔ [Spring Boot WebSocket Broker]
│
↓
[Pub/Sub Publisher → Google Pub/Sub Topic]
↑
[Pub/Sub Subscriber → WebSocket Broker]


1. **Front-end**
    - Thymeleaf + WebJars (Bootstrap, jQuery, SockJS, STOMP).
    - Page unique (`index.html`) qui se connecte au broker WebSocket embarqué et échange des `MessageDTO`.
2. **Back-end**
    - **ChatController**
        - `@MessageMapping("/chat.sendMessage")` → convertit la `MessageDTO` reçue, ajoute un timestamp si nécessaire, la publie sur le topic Pub/Sub (`floriantheo-common-topic`), et la renvoie sur `/topic/common`.
        - `@MessageMapping("/chat.addUser")` → ajoute l’utilisateur en session et diffuse un message système.
    - **PubSubSubscriptionService**
        - Deux adapters `PubSubInboundChannelAdapter` pour récupérer **manuellement** les messages de vos subscriptions (`sub-floriantheo-common`, `sub-floriantheo-user`) et les renvoyer sur les canaux Spring Integration.
        - Deux handlers `@ServiceActivator` qui envoient via `SimpMessagingTemplate` vers les topics WebSocket correspondants et `ack()` les messages.
3. **Pub/Sub**
    - Topics :
        - `floriantheo-common-topic`
        - `floriantheo-user-topic`
    - Subscriptions :
        - `sub-floriantheo-common`
        - `sub-floriantheo-user`

---

## ⚙️ Prérequis

- Java 17+ (Corretto, OpenJDK…)
- Maven 3.8+
- GCP SDK (optionnel si vous utilisez un emulator)
- Compte de service JSON avec les droits Pub/Sub
- Si local : l’émulateur Pub/Sub

---

## 🚀 Lancer en local

1. **Désactiver le healthcheck Pub/Sub**
   ```yaml
   management:
     health:
       pubsub:
         enabled: false
   

Ouvrir http://localhost:8080/ dans votre navigateur.


🚧 Problème actuel
JacksonPubSubMessageConverter n’arrive pas à désérialiser la MessageDTO reçue :

javascript
Copier
Modifier
Cannot construct instance of `com.floraintheo.chatrouter.MessageDTO`…
no String-argument constructor/factory method to deserialize from String value
Cause probable : votre MessageDTO est un record (ou n’a pas de constructeur public no-arg) et la conversion JSON → objet échoue.

Pistes de résolution :

Ajouter un constructeur par défaut ou annoté @JsonCreator.

Vérifier que vous publiez bien un objet et non une chaîne JSON brute (utiliser pubSubTemplate.publish(topic, chatMessage) et non publish(topic, jsonString) si vous voulez que la conversion automatique fonctionne).



