#!/bin/bash

# 1. Create a FRESH Order
USER_ID="d3316024-5d5e-4e4b-9721-657c91350a27" # Valid UUID
echo "Creating new order for User: $USER_ID..."
ORDER_ID=$(curl -s -X POST "http://localhost:8080/orders?userId=$USER_ID&amount=100.00" | jq -r '.id')
echo "Order ID: $ORDER_ID"

# 2. Fire 10 Payment Requests at ONCE (in parallel)
echo "Firing 10 concurrent requests..."

for i in {1..10}
do
   # Generate a unique Idempotency Key for each request to simulate distinct attempts
   # OR use the SAME key to test idempotency under load.
   # Let's use DIFFERENT keys to test the LOCK (Double Spending prevention).
   KEY=$(uuidgen)
   
   curl -s -X POST "http://localhost:8080/payments?orderId=$ORDER_ID" \
        -H "Idempotency-Key: $KEY" & # The '&' runs it in background (Parallel)
done

wait
echo "Done. Check logs to see only ONE success."