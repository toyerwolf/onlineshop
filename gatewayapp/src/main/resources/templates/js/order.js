import { refreshToken, logout } from './auth';

function makeOrder(productId) {
    // Log the productId for debugging purposes
    console.log('Product ID:', productId);

    // Retrieve access token from local storage
    let accessToken = localStorage.getItem('access_token');

    // Check if access token is missing
    if (!accessToken) {
        console.log('Access token is missing.');
        return;
    }

    // Use template literals for selector to find the product card
    const productCard = document.querySelector(`.product-card[data-product-id="${productId}"]`);
    // Log the selector used to find the product card
    console.log('Product Card Selector:', `.product-card[data-product-id="${productId}"]`);
    // Log the found product card element
    console.log('Product Card:', productCard);

    // Check if product card was not found
    if (!productCard) {
        console.error('Product card not found');
        return;
    }

    // Find the quantity input field within the product card
    const quantityInput = productCard.querySelector('.quantity-input');
    // Log the quantity input element
    console.log('Quantity Input:', quantityInput);

    // Parse the quantity input value to ensure it's a valid number
    const quantity = parseInt(quantityInput.value);
    // Log the parsed quantity value
    console.log('Quantity:', quantity);

    // Check if quantity is not a number or less than 1
    if (isNaN(quantity) || quantity < 1) {
        alert('Invalid quantity.');
        return;
    }

    // Create an order request object with product quantities
    const orderRequest = {
        productQuantities: {}
    };

    // Add the selected product and its quantity to the order request
    orderRequest.productQuantities[productId] = quantity;

    // Function to fetch the order
    function fetchOrder() {
        fetch('/home/makeOrder', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + accessToken
            },
            body: JSON.stringify(orderRequest)
        })
            .then(response => {
                // Handle case where response status is 401 (Unauthorized)
                if (response.status === 401) {
                    // Attempt to refresh access token and retry order request
                    return refreshToken().then(({ newAccessToken, newRefreshToken }) => {
                        accessToken = newAccessToken;
                        localStorage.setItem('access_token', newAccessToken);
                        localStorage.setItem('refresh_token', newRefreshToken);
                        return fetchOrder();
                    });
                }
                // Handle case where response is not ok
                if (!response.ok) {
                    // Parse response body as JSON and throw an error with its message
                    return response.json().then(error => {
                        throw new Error(error.message);
                    });
                }
                // Parse response body as JSON and return the data
                return response.json();
            })
            .then(data => {
                // Log success message with the returned data
                console.log('Order placed successfully:', data);
                // Store the orderId in local storage
                localStorage.setItem('orderId', data.orderId);
                // Redirect user to order details page
                window.location.href = 'order-details';
            })
            .catch(error => {
                // Check if error message indicates insufficient quantity
                if (error.message.includes('Insufficient quantity')) {
                    alert('Insufficient quantity for the selected product.');
                } else if (error.message === 'Insufficient balance') {
                    // Handle case where error message indicates insufficient balance
                    alert('Insufficient balance. Please add funds to your account.');
                } else if (error.message === 'Unauthorized') {
                    // Redirect user to login page if error message indicates unauthorized access
                    window.location.href = '/login';
                } else {
                    // Handle other errors with an alert and redirect to login page
                    alert('Error: ' + error.message);
                    window.location.href = '/login';
                }
                // Log the error message to the console
                console.error('Error making order:', error);
            });
    }

    // Initiate the fetchOrder function to start the order process
    fetchOrder();
}