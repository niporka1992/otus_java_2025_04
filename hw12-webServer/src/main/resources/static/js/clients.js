function getAllUsers() {
    fetch('/api/client/')
        .then(function (response) {
            if (response.status === 401) {
                alert("Нужно авторизоваться!");
                window.location.href = '/login';
                return [];
            }
            return response.json();
        })
        .then(function (clients) {
            var container = document.getElementById('clientsContainer');
            container.innerHTML = "";

            if (!clients || clients.length === 0) {
                container.innerHTML = '<p class="empty-msg">Нет клиентов</p>';
                return;
            }

            clients.forEach(function (c, index) {
                var card = document.createElement('div');
                card.className = "client-card";

                var phonesHtml = "";
                if (c.phones && c.phones.length > 0) {
                    for (var i = 0; i < c.phones.length; i++) {
                        phonesHtml += '<span class="phone">' + c.phones[i] + '</span>';
                    }
                } else {
                    phonesHtml = "<span class='phone empty'>нет телефонов</span>";
                }

                card.innerHTML =
                    '<div class="client-header">' +
                    '<span class="client-id">#' + (c.id ? c.id : (index + 1)) + '</span>' +
                    '<span class="client-name">' + c.name + '</span>' +
                    '</div>' +
                    '<div class="client-body">' +
                    '<div class="client-address">' + (c.street ? c.street : "-") + '</div>' +
                    '<div class="client-phones">' + phonesHtml + '</div>' +
                    '</div>';

                container.appendChild(card);
            });
        })
        .catch(function (error) {
            alert(error.message);
        });
}

function showCreateForm() {
    document.getElementById('createForm').style.display = 'block';
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
    const address = document.getElementById("newClientAddress").value.trim();
    const phones = Array.from(document.querySelectorAll(".phone-input"))
        .map(inp => inp.value.trim())
        .filter(v => v.length > 0);

    if (!name) {
        alert("Введите имя клиента");
        return;
    }

    const payload = { name, street: address, phones };

    fetch('/api/client/', {
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
        .catch(err => alert(err.message));
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

