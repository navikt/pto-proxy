# pto-proxy
Tynn proxy som videresender kall på tvers av clustere

## API-GW
For å kunne snakke med api-gw så trengs det en nøkkel pr applikasjon.
Disse ligger i secreten **pto-proxy-api-gw-keys**.

Hent nøkkler:
`kubectl describe secret pto-proxy-api-gw-keys`

Endre nøkkel:
`kubectl edit secret pto-proxy-api-gw-keys`

Lag secret:
`kubectl create secret generic pto-proxy-api-gw-keys --from-literal=API_GW_KEY_APP_1=<secret> --from-literal=API_GW_KEY_APP_2=<secret> `]

API-GW nøkklene blir lagt i vault og må legges inn som en k8s secret manuelt.

```shell script
kubectl create secret generic pto-proxy-api-gw-keys \
--from-literal=API_GW_KEY_VEILARBAKTIVITET=XXX \
--from-literal=API_GW_KEY_VEILARBDIALOG=XXX \
--from-literal=API_GW_KEY_VEILARBJOBBSOKERKOMPETANSE=XXX \
--from-literal=API_GW_KEY_VEILARBLEST=XXX \
--from-literal=API_GW_KEY_VEILARBOPPFOLGING=XXX \
--from-literal=API_GW_KEY_VEILARBPERSON=XXX \
--from-literal=API_GW_KEY_VEILARBVEDTAKINFO=XXX \
--from-literal=API_GW_KEY_FRONTENDLOGGER=XXX
```

### Hvordan registrere i api-gw
https://stackoverflow.com/c/nav-it/questions/324

Bruk `/rest/v2/katalog/applikasjoner` for å oppdatere i api-management, dette vil ikke påvirke api-gw før man bruker `/rest/v2/register/deploy/` for å pushe ut endringene.
Endringene må separat pushes ut til hvert miljø som f.eks q0,q1 og prod.

`curl -u NAV_IDENT:PASSWORD -v -H "kilde: noFasit" -H "Content-Type: application/json" -XPUT https://api-management.nais.adeo.no/rest/v2/katalog/applikasjoner/<APP> -d @api-gw/<APP>/katalog.json`

`curl -u NAV_IDENT:PASSWORD -v -H "kilde: noFasit" -H "Content-Type: application/json" -XPUT https://api-management.nais.adeo.no/rest/v2/register/deploy/<APP> -d @api-gw/<APP>/register_(q1/q0/prod).json` 

Sørg for å sjekke hva som ligger i configen med endepunktet under før man gjør endringer på api-gw configen.
https://api-management.nais.adeo.no/rest/v2/katalog/innhold/<APP>
 
 
