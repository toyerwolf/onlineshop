function toggleFavorite(element) {
    const isFavorite = element.classList.toggle('favorite');
    if (isFavorite) {
        addToWishlist(element);
    } else {
        removeFromWishlist(element);
    }
}

function loadFavorites() {
    const productCards = document.querySelectorAll('.product-card');
    const customerId = localStorage.getItem('customerId');

    if (!customerId) {
        console.error('Customer ID not found in localStorage');
        return;
    }

    // Загрузка избранных продуктов для текущего пользователя
    const favorites = JSON.parse(localStorage.getItem(`favorites_${customerId}`)) || [];

    // Применение статуса избранного к каждой карточке продукта
    productCards.forEach(card => {
        const productId = card.getAttribute('data-product-id');
        const heart = card.querySelector('.heart');

        if (favorites.includes(productId)) {
            heart.classList.add('favorite');
        } else {
            heart.classList.remove('favorite');
        }
    });
}

// Функция для сохранения состояния сердечка при добавлении или удалении из избранного
function addToWishlist(element) {
    const productCard = element.closest('.product-card');
    if (!productCard) {
        console.error('Parent product-card not found');
        return;
    }

    const productId = productCard.getAttribute('data-product-id');
    const customerId = localStorage.getItem('customerId'); // Получаем customerId из localStorage
    const accessToken = localStorage.getItem('access_token'); // Получаем accessToken из localStorage

    if (!customerId || !accessToken) {
        console.error('Customer ID or AccessToken not found in localStorage');
        return;
    }

    // Загружаем текущий список избранных продуктов пользователя
    let favorites = JSON.parse(localStorage.getItem(`favorites_${customerId}`)) || [];

    // Добавляем productId в список избранных, если его там еще нет
    if (!favorites.includes(productId)) {
        favorites.push(productId);
    }

    // Сохраняем обновленный список избранных продуктов в localStorage
    localStorage.setItem(`favorites_${customerId}`, JSON.stringify(favorites));

    // Отправляем запрос на сервер для добавления в избранное
    const url = '/wishlist/addProduct'; // Замените на корректный URL вашего эндпоинта
    const data = { customerId: customerId, productId: productId };

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}` // Добавляем accessToken в заголовки запроса
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                // Успешно добавлено в избранное
                console.log('Product added to wishlist successfully');
                element.classList.add('favorite'); // Добавляем класс 'favorite' для изменения сердечка
            } else {
                console.error('Failed to add product to wishlist');
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

// Функция для удаления продукта из избранного
function removeFromWishlist(element) {
    const productCard = element.closest('.product-card');
    if (!productCard) {
        console.error('Parent product-card not found');
        return;
    }

    const productId = productCard.getAttribute('data-product-id');
    const customerId = localStorage.getItem('customerId');
    const accessToken = localStorage.getItem('access_token');

    if (!customerId || !accessToken) {
        console.error('Customer ID or AccessToken not found in localStorage');
        return;
    }

    const url = `/wishlist/removeProduct?customerId=${customerId}&productId=${productId}`; // Adjusted URL

    fetch(url, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`
        }
    })
        .then(response => {
            if (response.ok) {
                console.log('Product removed from wishlist successfully');
                element.classList.remove('favorite');
            } else {
                console.error('Failed to remove product from wishlist');
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

// Вызов loadFavorites() при загрузке страницы
window.addEventListener('load', loadFavorites);


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


function incrementQuantity(element) {
    const productCard = element.closest('.product-card');
    const quantityInput = productCard.querySelector('.quantity-input');
    let value = parseInt(quantityInput.value);
    if (!isNaN(value)) {
        quantityInput.value = value + 1;
    }
}

function decrementQuantity(element) {
    const productCard = element.closest('.product-card');
    const quantityInput = productCard.querySelector('.quantity-input');
    let value = parseInt(quantityInput.value);
    if (!isNaN(value) && value > 1) {
        quantityInput.value = value - 1;
    }
}

function rateProduct(starElement) {
    const productId = starElement.closest('.product-card').getAttribute('data-product-id');
    const ratingValue = parseInt(starElement.getAttribute('data-value'));

    const accessToken = localStorage.getItem('access_token');
    if (!accessToken) {
        console.error('Access token not found. Unable to rate product.');
        return; // Выходим из функции, если токен отсутствует
    }

    // Проверяем срок действия токенов перед отправкой запроса
    checkTokenExpiration();

    const customerId = localStorage.getItem('customerId');

    const ratingData = {
        rating: ratingValue,
        productId: productId,
        customerId: customerId
    };

    // Отправляем запрос на сервер для оценки продукта
    fetch(`/ratings/${productId}/rate`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + accessToken
        },
        body: JSON.stringify(ratingData)
    })
        .then(response => {
            if (!response.ok) {
                if (response.status === 401) { // Проверяем статус 401 (Unauthorized)
                    logout(); // Вызываем logout при неавторизованном доступе
                    throw new Error('Unauthorized: User logged out');
                }
                throw new Error('Failed to rate product');
            }
            return response.json(); // Получаем JSON-ответ
        })
        .then(data => {
            console.log('Product rated successfully:', data.message);
            // Сохраняем оценку в localStorage
            localStorage.setItem(`product_${productId}_rating`, ratingValue.toString());
            // Обновляем звездочки для текущего продукта
            updateStars(productId, ratingValue);
            // Обновляем общий рейтинг продукта
            updateAverageRating(productId);
        })
        .catch(error => {
            console.error('Error rating product:', error);
        });
}