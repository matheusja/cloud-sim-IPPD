# Para compilar

`mvn assembly:assembly -DdescriptorId=jar-with-dependencies`

# Para rodar

`java -cp target/cloud-sim-trabalho-1.0-SNAPSHOT-jar-with-dependencies.jar app.App`

Pode haver um argumento opcional: "small", "medium" ou "big", por padrão, é small, define quais arquivos vão ser utilizados(estão em resources/jobs/<NOME DA INSTITUIÇÃO>_<nome da classe de tamanho>.tsv)

Como segundo argumento opcional, pode ser especificado um de { `crude`, `refined`, `default` } para especificar uma estratégia de escalonamento de cloudlets em máquinas virtuais. Se nenhum desses é especificado, é assumido `default`.