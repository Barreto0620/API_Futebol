#!/bin/bash

API_KEY="your-secret-api-key-here"
BASE_URL="http://localhost:8080"

echo "Testing rate limits..."

# Test GET /times endpoint (limit: 50 requests/minute)
for i in {1..55}; do
    response=$(curl -s -w "%{http_code}" -H "X-API-Key: $API_KEY" $BASE_URL/times)
    http_code=${response: -3}
    
    if [ $http_code -eq 200 ]; then
        echo "Request $i: Success"
    elif [ $http_code -eq 429 ]; then
        echo "Request $i: Rate limit exceeded (expected after 50 requests)"
        break
    else
        echo "Request $i: Unexpected status code: $http_code"
    fi
    
    sleep 0.1 # Small delay between requests
done

echo "Test complete!"