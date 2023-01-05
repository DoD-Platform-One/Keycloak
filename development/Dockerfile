FROM registry1.dso.mil/ironbank/redhat/ubi/ubi8-micro:8.7

RUN mkdir /app

WORKDIR /app

COPY ./theme-custom/custom-theme.jar /app

RUN chmod -R +rx /app
