Camel Router Project
====================

to compile this proyect you have to run the next command and put into the service mix

    mvn clean package

**List of rest request**
  1. POST /conektaApi/token
      curl -X "POST" "http://localhost:2137/conektaApi/token" \
           -H "Content-Type: application/json; charset=utf-8" \
           -d "{\"creditCard\":\"1234567890123456\",\"buyerName\":\"ramses carbajal\",\"bin\":\"t2152r\",\"fechaExp\":\"12/21\",\"creditCardSchema\":\"1\",\"marcaCardMarca\":\"3\"}"

  2. POST /conektaApi/purchase
    - curl -X "POST" "http://localhost:2137/conektaApi/purchase" \
           -H "Content-Type: application/json; charset=utf-8" \
           -d "{\"token\":\"dt0FWJRmNB7nkLk2RE9xgHmZ+UAK1O0a\",\"amount\":\"123d3333.434\"}"


you can test this request in this server 

	52.42.136.136:9001