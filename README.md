# Detector de mutantes
_Sr Magneto. Le presento a la última versión del detector de mutantes_
El mismo le permitirá identificar si un individuo es humano o mutante con una simple muestra de ADN
Puede consultarlo en la URL **http://35.199.122.12/**

El mismo expone dos endpoints:
* **/mutant** | Recibe un adn y determina si es mutante (Status - 200) o humano (Status - 403)

```
curl --location --request POST 'http://35.199.122.12/mutant' \
--header 'Content-Type: application/json' \
--data-raw '{
    "dna": [
        "ATTCGA",
        "CAGTGC",
        "TTCTGC",
        "AAGAGC",
        "CCCCTA",
        "TAACTG"
    ]
}'
```

* **/stats** | Devuelve estadísticas sobre la cantidad y proporción de mutantes encontrados

```
curl --location --request GET 'http://35.199.122.12/stats'
```

### Pre-requisitos 📋

_Dependencias:_

```
* Gradle
* Docker
* Groovy
* Java
```

_Antes de poner en marcha el API necesitamos_

Una base de datos NoSQL - Neo4j
```
Usuario: neo4j
Password: s3cr3t (Se puede cambiar luego de iniciara por primera vez sesión usando "neo4j" como usuario y password
Puertos 7474 (Browser), 7687 (bolt) y 6000 (tx) expuestos
```

Una base de datos NoSQL - Redis (con configuración default)
```
Puerto 6379 expuesto
```

### Pre-requisitos (docker) 📋

_Levantar la base redis_
```
docker run -d -p 6379:6379 --name mutant-redis redis
```

_Levantar la base neo4j_
```
docker run -d -p 7474:7474 -p 7687:7687 -p 6000:6000 -e NEO4J_AUTH=neo4j/s3cr3t --name mutant-neo4j neo4j
```

_Crear una network en común_
```
docker network create mutant-detector-net
```

_Conectar contenedor a la red (Aplicar con el container de redis, neo4j y el de la app)_
```
docker network connect mutant-detector-net <containerNameOrId>
```

### Instalación 🔧

_1) Antes de iniciar debemos tomar nota de las IP de las bases REDIS y Neo4j para hacerlo determinamos la IP de sus containers usando_
```
docker inspect <containerNameOrId>
```
_2) Modificar_ 
  * La property "neo4j.url" indicando la IP del container para neo4j
  * El valor del constructor de Jedis en RedisAdapter con la IP del container de redis
_3) Ir al directorio de la aplicación_
```
cd $APP_DIR
```
_4) Compilar el proyecto_
```
./gradlew build
```
_5) Copiar el JAR (build/libs/mutantes-1.6.jar a la carpeta del proyecto)
_6) Generar la imagen de docker_
```
docker build -t magneto/mutant-detector:1.0 .
```
_7) Correr la imagen_
```
docker run -p 8080:8080 --name mutant-detector magneto/mutant-detector:1.0
```
_7) Conectar el contenedor a la red_
```
docker network connect mutant-detector-net <containerNameOrId>
```

_Podemos probar usando curl, postman u otro similar_
```
curl http://localhost:8080/stats
```

## Ejecutando las pruebas ⚙️

_Podemos ejecutar las pruebas automatizadas con la tarea 'verify' de gradle_
```
./gradlew verify
```

### Coverage

_Podemos ejecutar un análisis de cobertura ejecutando la tarea 'jacocoTestReport' de gradle_

```
./gradlew jacocoTestReport
```

Esto dejará un informe en **build/reports/jacocoHtml/index.html**

## Autor ✒️

* **Lucas Costas** - [FedericoGarou](https://github.com/FedericoGarou)