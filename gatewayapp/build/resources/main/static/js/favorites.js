export function toggleFavorite(element) {
    const isFavorite = element.classList.toggle('favorite');
    if (isFavorite) {
        addToWishlist(element);
    } else {
        removeFromWishlist(element);
    }
}

export function loadFavorites() {
    const productCards = document.querySelectorAll('.product-card');
    const customerId = localStorage.getItem('customerId');

    if (!customerId) {
        console.error('Customer ID not found in localStorage');
        return;
    }

    const favorites = JSON.parse(localStorage.getItem(`favorites_${customerId}`)) || [];

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

function addToWishlist(element) {
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

    let favorites = JSON.parse(localStorage.getItem(`favorites_${customerId}`)) || [];

    if (!favorites.includes(productId)) {
        favorites.push(productId);
        localStorage.setItem(`favorites_${customerId}`, JSON.stringify(favorites));

        const url = '/wishlist/addProduct';
        const data = { customerId: customerId, productId: productId };

        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (response.ok) {
                    console.log('Product added to wishlist successfully');
                    element.classList.add('favorite');
                } else {
                    console.error('Failed to add product to wishlist');
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }
}

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

    let favorites = JSON.parse(localStorage.getItem(`favorites_${customerId}`)) || [];
    favorites = favorites.filter(id => id !== productId);
    localStorage.setItem(`favorites_${customerId}`, JSON.stringify(favorites));

    const url = `/wishlist/removeProduct?customerId=${customerId}&productId=${productId}`;

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

// Добавляем экспорт loadFavorites