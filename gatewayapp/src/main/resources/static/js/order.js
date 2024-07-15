import { refreshToken, logout } from './auth.js';

export function makeOrder(productId) {
    console.log('Product ID:', productId);

    let accessToken = localStorage.getItem('access_token');
    if (!accessToken) {
        console.log('Access token is missing.');
        return;
    }

    const productCard = document.querySelector(`.product-card[data-product-id="${productId}"]`);
    console.log('Product Card Selector:', `.product-card[data-product-id="${productId}"]`);
    console.log('Product Card:', productCard);

    if (!productCard) {
        console.error('Product card not found');
        return;
    }

    const quantityInput = productCard.querySelector('.quantity-input');
    console.log('Quantity Input:', quantityInput);

    const quantity = parseInt(quantityInput.value);
    console.log('Quantity:', quantity);

    if (isNaN(quantity) || quantity < 1) {
        alert('Invalid quantity.');
        return;
    }

    const orderRequest = {
        productQuantities: {}
    };

    orderRequest.productQuantities[productId] = quantity;

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
                if (response.status === 401) {
                    return refreshToken().then(({ newAccessToken, newRefreshToken }) => {
                        accessToken = newAccessToken;
                        localStorage.setItem('access_token', newAccessToken);
                        localStorage.setItem('refresh_token', newRefreshToken);
                        return fetchOrder();
                    });
                }
                if (!response.ok) {
                    return response.json().then(error => {
                        throw new Error(error.message);
                    });
                }
                return response.json();
            })
            .then(data => {
                console.log('Order placed successfully:', data);
                localStorage.setItem('orderId', data.orderId);
                window.location.href = 'order-details';
            })
            .catch(error => {
                if (error.message.includes('Insufficient quantity')) {
                    alert('Insufficient quantity for the selected product.');
                } else if (error.message === 'Insufficient balance') {
                    alert('Insufficient balance. Please add funds to your account.');
                } else if (error.message === 'Unauthorized') {
                    window.location.href = '/login';
                } else {
                    alert('Error: ' + error.message);
                    window.location.href = '/login';
                }
                console.error('Error making order:', error);
            });
    }

    fetchOrder();
}