
function hideCartErrorDiv()
    {
        var cartErrorDiv = document.getElementById('success_password');
        if(cartErrorDiv)
        {
            cartErrorDiv.style.display = 'none';
        }
    }
    setTimeout(hideCartErrorDiv ,6000)

    function passwordMessageDiv()
    {
        var passwordId = document.getElementById('error_password');
        if(passwordId){
         passwordId.style.display='none';

        }
    }
    setTimeout(passwordMessageDiv,6000);