function loadCurrentCurrencies() {
    fetch('/api/currencies/current?base=USD') // Меняйте параметр base в зависимости от вашего выбора
        .then(response => {
            if (!response.ok) {
                throw new Error('Ошибка сети при загрузке данных о валютах');
            }
            return response.json();
        })
        .then(data => {
            // Обработка полученных данных
            console.log('Полученные данные о валютах:', data);

            // Пример вывода данных в консоль
            console.log(`Курс доллара: ${data.rates.USD}`);
            console.log(`Курс евро: ${data.rates.EUR}`);
            console.log(`Курс фунта: ${data.rates.GBP}`);
            console.log(`Курс рубля: ${data.rates.RUB}`);
            console.log(`Курс лиры: ${data.rates.TRY}`);
            console.log(`Курс маната: ${data.rates.AZN}`);

            // Вставка данных на страницу
            const currencyContainer = document.getElementById('currency-container');
            currencyContainer.innerHTML = `
                <h3>Currency Rates</h3>
                <ul>
                    <li>USD: $ ${data.rates.USD.toFixed(2)}</li>
                    <li>EUR: € ${data.rates.EUR.toFixed(2)}</li>
                    <li>GBP: £ ${data.rates.GBP.toFixed(2)}</li>
                    <li>RUB: ₽ ${data.rates.RUB.toFixed(2)}</li>
                    <li>TRY: ₺ ${data.rates.TRY.toFixed(2)}</li>
                    <li>AZN: ₼ ${data.rates.AZN.toFixed(2)}</li>
                </ul>
            `;
        })
        .catch(error => {
            console.error('Ошибка загрузки данных о валютах:', error);
        });
}

// Вызов функции загрузки данных при загрузке страницы
window.onload = loadCurrentCurrencies;