#!/usr/bin/env python3

import os
import socket
import time

service = os.getenv("HEADLESS_SERVICE")
port = int(os.getenv("PORT"))
timeout = int(os.getenv("TIMEOUT"))

# Extract IP addresses
host_info = socket.gethostbyname_ex(service)
ip_addresses = host_info[2]

for ip in ip_addresses:
    try:
        sock = socket.create_connection((ip, port))
        sock.settimeout(timeout)
        sock.sendall(b'ping from helm test')

        # Sleep to allow enough time for connection to register in logs
        time.sleep(.5)
        response = sock.recv(1)
        print(f"Connection to {ip} on port {port} was successful")

    except Exception as e:
        raise ConnectionError(f"Failed to connect to {ip} on port {port} with exception: {e}") from e
    finally:
        sock.close()