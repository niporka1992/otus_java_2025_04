function getAllUsers() {
    fetch('/api/clients')
        .then(response => response.json())
        .then(clients => {
            const container = document.getElementById('clientsContainer');
            container.innerHTML = "";

            if (!clients || clients.length === 0) {
                container.innerHTML = '<p class="empty-msg">Нет клиентов</p>';
                return;
            }

            clients.forEach(function (c, index) {
                const card = document.createElement('div');
                card.className = "client-card";

                // Формируем телефоны (phones — массив строк!)
                let phonesHtml = "";
                if (c.phones && c.phones.length > 0) {
                    phonesHtml = c.phones
                        .map(phone => `<span class="phone">${phone}</span>`)
                        .join('');
                } else {
                    phonesHtml = "<span class='phone empty'>нет телефонов</span>";
                }

                // Формируем адрес (street — строка, а не объект)
                const addressHtml = c.street ? c.street : "-";

                card.innerHTML = `
                    <div class="client-header">
                        <span class="client-id">#${c.id ? c.id : index + 1}</span>
                        <span class="client-name">${c.name}</span>
                    </div>
                    <div class="client-body">
                        <div class="client-address">${addressHtml}</div>
                        <div class="client-phones">${phonesHtml}</div>
                    </div>
                `;

                container.appendChild(card);
            });
        })
        .catch(error => {
            alert("Ошибка загрузки клиентов: " + error.message);
        });
}


function addPhoneField() {
    const container = document.getElementById("phonesContainer");
    const input = document.createElement("input");
    input.type = "text";
    input.className = "phone-input";
    input.placeholder = "Телефон";
    container.appendChild(document.createElement("br"));
    container.appendChild(input);
}

function createClient() {
    const name = document.getElementById("newClientName").value.trim();
    const street = document.getElementById("newClientAddress").value.trim();
    const phones = Array.from(document.querySelectorAll(".phone-input"))
        .map(inp => inp.value.trim())
        .filter(v => v.length > 0);

    if (!name) {
        alert("Введите имя клиента");
        return;
    }

    // 👇 Отправляем street как плоское поле, а не address
    const payload = {
        name,
        street: street || null,
        phones
    };

    fetch('/api/clients', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
        .then(res => {
            if (!res.ok) throw new Error("Ошибка сохранения клиента");
            return res.json();
        })
        .then(data => {
            alert("Клиент создан!");
            document.getElementById("createForm").style.display = "none";
            getAllUsers();
        })
        .catch(err => alert("Ошибка: " + err.message));
}


function showClients() {
    const block = document.getElementById("clientsBlock");
    block.style.display = "block";

    document.getElementById("closeClientsBtn").style.display = "inline-block";

    getAllUsers();
}

function showCreateForm() {
    const form = document.getElementById("createForm");
    form.style.display = "block";

    document.getElementById("closeFormBtn").style.display = "inline-block";
}

function closeBlock(id) {
    const block = document.getElementById(id);
    block.style.display = "none";

    if (id === "clientsBlock") {
        document.getElementById("closeClientsBtn").style.display = "none";
    }
    if (id === "createForm") {
        document.getElementById("closeFormBtn").style.display = "none";
    }
}
