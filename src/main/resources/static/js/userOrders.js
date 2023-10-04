

document.addEventListener("DOMContentLoaded", function() {
    const statusCheckboxes = document.querySelectorAll('input[name="status"]');
    const timeCheckboxes = document.querySelectorAll('input[name="time"]');

    statusCheckboxes.forEach(checkbox => {
        checkbox.addEventListener("change", applyFilters);
    });

    timeCheckboxes.forEach(checkbox => {
        checkbox.addEventListener("change", applyFilters);
    });

    function applyFilters() {
        const selectedStatusFilters = Array.from(document.querySelectorAll('input[name="status"]:checked')).map(checkbox => checkbox.value);
        const selectedTimeFilters = Array.from(document.querySelectorAll('input[name="time"]:checked')).map(checkbox => checkbox.value);
        console.log(selectedStatusFilters);
        console.log(selectedTimeFilters);
         const dataToSend = JSON.stringify({
                    statusFilters: selectedStatusFilters,
                    timeFilters: selectedTimeFilters
                });

        $.ajax({
            url: '/userOrderFilter',
            type: 'POST',
            contentType: 'application/json',
            data: dataToSend,
            success: function(success) {
                console.log(success);
            },
            error: function(error) {
                console.error("Error:", error);
            }
        });
    }
});
