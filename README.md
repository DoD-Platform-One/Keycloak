# Running Cypress in Docker 

## Command

### Headless
```bash
docker run -it --rm -d --network host -v $PWD:/e2e -w /e2e cypress/included:3.2.0
```

### Interactive
```bash
docker run -it \
  --rm \
  -v $PWD:/e2e \
  -w /e2e \
  --network host \
  -e DISPLAY=$(ifconfig en0 | grep inet | awk '$1=="inet" {print $2}'):0 \
  -v ~/.Xauthority:/root/.Xauthority:ro \
  --entrypoint cypress \
  cypress/included:3.2.0 open --project . --config baseUrl=http://host.docker.internal:8080
  ```

## Resources

### Running Cypress in docker
https://www.cypress.io/blog/2019/05/02/run-cypress-with-a-single-docker-command/

### Running xquartz on mac resources
linked in Running Cypress in Docker
https://sourabhbajaj.com/blog/2017/02/07/gui-applications-docker-mac/

### Different socket for mac
https://stackoverflow.com/questions/59514234/viewing-the-interactive-cypress-test-runner-in-docker-on-linux#answer-59518537
