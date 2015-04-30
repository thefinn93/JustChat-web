# JustChat Web

The web crap that runs our server crap. By request, the openssl command use to generate our server-side SSL key and CSR:

```
openssl req -sha256 -out justchat.finn.ninja.csr -new -newkey rsa:4096 -nodes -keyout justchat.finn.ninja.key
```
