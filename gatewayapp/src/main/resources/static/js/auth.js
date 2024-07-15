function showMakeOrderButtonAndQuantity() {
    const makeOrderButtons = document.querySelectorAll('.make-order-button');
    const quantityInputs = document.querySelectorAll('.quantity-input');
    const quantityButtons = document.querySelectorAll('.quantity-btn');

    makeOrderButtons.forEach(button => button.style.display = 'block');
    quantityInputs.forEach(input => input.style.display = 'block');
    quantityButtons.forEach(button => button.style.display = 'block');
}

function hideMakeOrderButtonAndQuantity() {
    const makeOrderButtons = document.querySelectorAll('.make-order-button');
    const quantityInputs = document.querySelectorAll('.quantity-input');
    const quantityButtons = document.querySelectorAll('.quantity-btn');

    makeOrderButtons.forEach(button => button.style.display = 'none');
    quantityInputs.forEach(input => input.style.display = 'none');
    quantityButtons.forEach(button => button.style.display = 'none');
}

function fetchUserInfo(accessToken) {
    fetch('/user/me', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + accessToken
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch user info');
            }
            return response.json();
        })
        .then(data => {
            showMakeOrderButtonAndQuantity(); // Добавляем вызов функции showMakeOrderButtonAndQuantity()
            document.getElementById('auth-links-logged-out').style.display = 'none';
            document.getElementById('auth-links-logged-in').style.display = 'block';
            document.getElementById('customer-name').textContent = data.name;
            localStorage.setItem('customerId', data.id);
            localStorage.setItem('customerName', data.name);
        })
        .catch(error => {
            console.error('Error fetching user info:', error);
            logout();
        });
}

export function refreshToken() {
    const refreshToken = localStorage.getItem('refresh_token');
    return fetch('/home/refresh', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ refreshToken: refreshToken })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to refresh access token');
            }
            return response.json();
        })
        .then(data => {
            const { accessToken, refreshToken } = data;
            const currentTime = new Date().getTime();
            const accessTokenExpiration = currentTime + 30000; // 30 секунд
            const refreshTokenExpiration = currentTime + 60000; // 1 минута

            localStorage.setItem('access_token', accessToken);
            localStorage.setItem('access_token_expiration', accessTokenExpiration.toString());
            localStorage.setItem('refresh_token', refreshToken);
            localStorage.setItem('refresh_token_expiration', refreshTokenExpiration.toString());
        });
}

export function logout() {
    // Очистить токены и другие данные пользователя
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('access_token_expiration');

    // Перенаправить пользователя на страницу входа или выполнить другие действия
    window.location.href = '/login';
}

export async function checkTokenExpiration() {
    try {
        const refreshTokenExpiration = localStorage.getItem('refresh_token_expiration');
        const currentTime = Math.floor(new Date().getTime() / 1000); // Округляем текущее время в секундах

        if (refreshTokenExpiration && currentTime > parseInt(refreshTokenExpiration)) {
            logout(); // Если refreshToken истек, разлогиниваем пользователя
        } else {
            await refreshToken(); // Обновляем refreshToken, если он действителен
        }
    } catch (error) {
        console.error('Error checking token expiration:', error);
    }
}


function checkTokenAvailability() {
    const accessToken = localStorage.getItem('access_token');
    const productCards = document.querySelectorAll('.product-card');

    productCards.forEach(card => {
        const makeOrderButton = card.querySelector('button[data-action="make-order"]');
        if (makeOrderButton) {
            if (accessToken) {
                makeOrderButton.style.display = 'block';
            } else {
                makeOrderButton.style.display = 'none';
            }
        }

        const quantityButtons = card.querySelectorAll('.quantity-btn');
        const quantityInput = card.querySelector('.quantity-input');
        if (quantityInput) {
            if (accessToken) {
                quantityButtons.forEach(button => {
                    button.style.display = 'inline-block';
                });
                quantityInput.style.display = 'inline-block';
            } else {
                quantityButtons.forEach(button => {
                    button.style.display = 'none';
                });
                quantityInput.style.display = 'none';
            }
        }

        const ratingStars = card.querySelectorAll('.rating .star');
        if (ratingStars.length > 0) {
            if (accessToken) {
                ratingStars.forEach(star => {
                    star.style.display = 'inline-block';
                });
            } else {
                ratingStars.forEach(star => {
                    star.style.display = 'none';
                });
            }
        }
    });
}

setInterval(checkTokenExpiration, 75000);