const qrcodeContainer = document.getElementById('qrcode');
const connectButton = document.getElementById('connectBtn');
const errorDiv = document.getElementById('error');

async function getInvitationBase64() {
    errorDiv.innerHTML = '';
    try {
        const response = await fetch(BASE_PATH + 'create-invitation');

        if (!response.ok) {
            const errorBody = await response.text();
            throw new Error(errorBody);
        }

        const data = await response.json();
        return data.invitationUrl;
    } catch (error) {
        const p = document.createElement('p');
        p.classList.add('alert', 'alert-danger');
        p.textContent = error.message;
        errorDiv.appendChild(p);

        console.error('Invitation error:', error);
    }
}

async function generateQRcode(text) {
    console.log('Generate QR code to:', text);
    QRCode.toString(text, { type: 'svg' }, function (err, url) {
    if (err) throw err
        qrcodeContainer.innerHTML = url;
    });
}

connectButton.addEventListener('click', async () => {
    const invitationUrlBase64 = await getInvitationBase64();
    console.log(`invitationUrl: ${invitationUrlBase64}`)

    if (invitationUrlBase64) {
        generateQRcode(invitationUrlBase64);
    }
});
