

    function hideCartErrorDiv()
    {
        var cartErrorDiv = document.getElementById('cart_added');
        if(cartErrorDiv)
        {
            cartErrorDiv.style.display = 'none';
        }
    }
    setTimeout(hideCartErrorDiv ,4000)


 function hideCartErrorDiv2()
    {
        var cartErrorDiv = document.getElementById('no_stock');
        if(cartErrorDiv)
        {
            cartErrorDiv.style.display = 'none';
        }
    }
    setTimeout(hideCartErrorDiv2 ,4000)




    var selectElement = document.getElementById('selectedVariant');
    var selectedVariantPrice = document.getElementById('selectedVariantPrice');
     var defaultPrice = selectElement.options[0].getAttribute('data-variant-price');
    selectedVariantPrice.textContent = '₹ ' + defaultPrice;
    selectElement.addEventListener('change', function() {
        var selectedOption = selectElement.options[selectElement.selectedIndex];
        var price = selectedOption.getAttribute('data-variant-price');
        selectedVariantPrice.textContent = '₹ ' + price;
    });




    function changeMainImage(index) {
        // Get the main image and the product images
        var mainImage = document.getElementById('mainImage');
        var productImages = document.querySelectorAll('.product__details__pic__left a img');

        // Update the main image source with the clicked product image
        mainImage.src = productImages[index].src;
    }

    function changeMainImage(index) {
       var mainImage = document.getElementById('mainImage');
       var productImages = document.querySelectorAll('.product__details__pic__left a img');

       // Update the main image source with the clicked product image
       mainImage.src = productImages[index].src;
   }
 function changeMainImage(index) {
        var mainImage = document.getElementById('mainImage');
        var productImages = document.querySelectorAll('.product__details__pic__left a img');

        // Update the main image source with the clicked product image
        mainImage.src = productImages[index].src;
    }

    function zoomImage(event) {
        var mainImage = document.getElementById('mainImage');
        var zoomLens = document.getElementById('zoomLens');

        // Calculate the position of the lens
        var x = event.clientX - zoomLens.offsetWidth/1;
        var y = event.clientY - zoomLens.offsetHeight/1;

        // Ensure the lens does not move outside the boundaries of the main image container
        var container = document.getElementById('mainImageContainer');
        var containerRect = container.getBoundingClientRect();
        if (x < containerRect.left) x = containerRect.left;
        if (y < containerRect.top) y = containerRect.top;
        if (x > containerRect.right - zoomLens.offsetWidth) x = containerRect.right - zoomLens.offsetWidth;
        if (y > containerRect.bottom - zoomLens.offsetHeight) y = containerRect.bottom - zoomLens.offsetHeight;

        // Set the position of the lens to follow the cursor
        zoomLens.style.left = x + '20px';
        zoomLens.style.top = y + 'px';

        // Calculate the ratio between the main image and zoom lens
        var ratioX = (x - containerRect.left) / containerRect.width;
        var ratioY = (y - containerRect.top) / containerRect.height;

        // Set the zoom level of the main image using the transform property
        mainImage.style.transformOrigin = ratioX * 100 + '% ' + ratioY * 100 + '%';
        mainImage.style.transform = 'scale(2)'; // Adjust the zoom level as needed

        // Show the transparent zoom lens
        zoomLens.style.display = 'block';
    }

    // Function to hide the zoom lens when the mouse leaves the main image
    function hideZoomLens() {
        var zoomLens = document.getElementById('zoomLens');
        zoomLens.style.display = 'none';

        // Reset the zoom level of the main image
        var mainImage = document.getElementById('mainImage');
        mainImage.style.transform = 'scale(1)';
    }


