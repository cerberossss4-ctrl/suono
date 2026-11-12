# Suono

App Android (Kotlin + Jetpack Compose) per cercare e ascoltare brani del catalogo Jamendo tramite la sua API ufficiale. Jamendo ospita musica di artisti reali, pubblicata secondo licenze Creative Commons; le condizioni variano per brano e per tipo di utilizzo.

## Configurazione

1. Registra un'app nel [portale sviluppatori Jamendo](https://developer.jamendo.com/v3.0) e ottieni un `client_id`.
2. Configura l'ID senza inserirlo nel codice sorgente: crea `local.properties` nella root del progetto e aggiungi:

   ```properties
   JAMENDO_CLIENT_ID=il_tuo_client_id
   ```

   In alternativa, imposta la variabile d'ambiente `JAMENDO_CLIENT_ID`.
3. Apri il progetto in Android Studio, sincronizza Gradle e avvia il modulo `app` con una connessione Internet.

La schermata carica i brani più popolari e consente di cercare per artista o titolo. La riproduzione usa l'URL audio restituito dall'API e ExoPlayer.

## Licenze e rilascio

Questa integrazione non concede automaticamente diritti per ogni possibile uso. Prima di distribuire l'app, leggi e rispetta i [termini API Jamendo](https://developer.jamendo.com/v3.0) e la licenza indicata per ogni brano; verifica in particolare i diritti di streaming, uso commerciale, attribuzione, caching e riproduzione in background. Per un catalogo commerciale diverso o più ampio serve un accordo con il relativo titolare dei diritti.

Un client ID incorporato nell'app Android può essere estratto dal pacchetto: trattalo come identificativo pubblico e applica eventuali restrizioni/quote offerte dal provider. Non inserire chiavi segrete o token privati nell'app.
