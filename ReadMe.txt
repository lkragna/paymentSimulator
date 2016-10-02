Camel Router Project
====================

To run this router either embed the jar inside Spring
or to run the route from within maven try

    mvn camel:run

For more help see the Apache Camel documentation

    http://activemq.apache.org/camel/
**List of rest request**
  1. GET /{merchant}/categories
      - /tommymx/categories?lang=es_mx**
  2. GET /{merchant}/category/{slug}
    - /tommymx/category/mujer/ropa/polos?lang=es_mx
  3. GET /{merchant}/productsByCategory/{slug}
    - /tommymx/productsByCategory/mujer/ropa/polos?lang=es_mx&currency=mxn
  4. GET /{merchant}/product/{part_number}
    - /crateandbarrel/product/JHAJSHS90?lang=es_mx&currency=mxn
