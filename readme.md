A Stock App experiment using Micronaut / Micronaut Data

1. Build the application into a fat-jar:
`mvn clean package`

1. Build a Docker image version and latest:
`docker build -t nu-stocks:latest -t nu-stocks:[version] .`

1. Run the Docker image:
`docker run --rm -p 8080:8080 nu-stocks`