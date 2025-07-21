self.addEventListener('push', function(event) {
    const data = event.data.json();
    const title = data.title || '保育マッチングアプリ';
    const options = {
        body: data.message,
        icon: '/images/icon.png' // You might want to add an icon
    };

    event.waitUntil(self.registration.showNotification(title, options));
});

self.addEventListener('notificationclick', function(event) {
    event.notification.close();
    event.waitUntil(clients.openWindow('/')); // Open the app when notification is clicked
});
