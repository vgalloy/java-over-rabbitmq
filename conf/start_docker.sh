
docker run --hostname my-rabbit \
    -e RABBITMQ_DEFAULT_USER=root \
    -e RABBITMQ_DEFAULT_PASS=root \
    -p 29001:4369  \
    -p 29002:5671  \
    -p 29003:5672  \
    -p 29004:15671 \
    -p 29005:15672 \
    rabbitmq:3-management