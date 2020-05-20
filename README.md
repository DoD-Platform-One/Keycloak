# Execute cypress tests:
```bash
docker run -it --rm -d --network host -v $PWD:/e2e -w /e2e cypress/included:3.2.0
```