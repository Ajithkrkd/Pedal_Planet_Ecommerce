document.addEventListener("DOMContentLoaded", function () {
  // Initialize and render the chart here
  var options = {
    series: [{
      name: 'sales',
      data: []
    }],
    chart: {
      height: 350,
      type: 'bar',
    },
    plotOptions: {
      bar: {
        borderRadius: 10,
        dataLabels: {
          position: 'top',
        },
      }
    },
    dataLabels: {
      enabled: true,
      formatter: function (val) {
        return '₹' + val;
      }, // Remove the percentage formatting
      offsetY: -20,
      style: {
        fontSize: '12px',
        colors: ["#304758"]
      }
    },
    xaxis: {
      categories: [], // Will be updated with all months
      position: 'top',
      axisBorder: {
        show: false
      },
      axisTicks: {
        show: false
      },
      crosshairs: {
        fill: {
          type: 'gradient',
          gradient: {
            colorFrom: '#D8E3F0',
            colorTo: '#BED1E6',
            stops: [0, 100],
            opacityFrom: 0.4,
            opacityTo: 0.5,
          }
        }
      },
      tooltip: {
        enabled: true,
      }
    },
    yaxis: {
      axisBorder: {
        show: false
      },
      axisTicks: {
        show: false,
      },
      labels: {
        show: false,
        formatter: function (val) {
          return '₹' + val;
        }
      }
    },
    title: {
      text: 'Monthly Sales PEDAL PLANET',
      floating: true,
      offsetY: 330,
      align: 'center',
      style: {
        color: '#444'
      }
    }
  };






 var chart = new ApexCharts(document.querySelector("#chart1"), options);
  chart.render();

  // Get the select element
  var yearSelect = document.getElementById('year-select');

  // Add a "change" event listener to the select element
  yearSelect.addEventListener("change", function () {
    const selectedYear = yearSelect.value;

    // Fetch data based on the selected year
    fetch(`/admin/monthlySalesChart?year=${selectedYear}`)
      .then(response => response.json())
      .then(data => {
        // Initialize an array to store sales data for all months (initialized to 0)
        var allMonthsSales = Array(12).fill(0);

        // Extract the total sales data and month numbers from the received data
        data.forEach(item => {
          allMonthsSales[item.month - 1] = item.totalSales;
        });

        // Get month names
        var monthNames = getMonthNames();

        // Update the x-axis categories with month names
        chart.updateOptions({
          xaxis: {
            categories: monthNames
          }
        });

        // Update the chart series with the extracted sales data
        chart.updateSeries([{
          data: allMonthsSales
        }]);
      })
      .catch(error => console.error('Error fetching data:', error));
  });

  // Function to get month names
  function getMonthNames() {
    return [
      'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
      'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'
    ];
  }
});

document.addEventListener("DOMContentLoaded", function () {
    fetch('/admin/payment_data')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(paymentMethodCounts => {

            var options = {
                series: paymentMethodCounts,
                chart: {
                    width: 500,
                    type: 'pie',
                },
                // Labels can be provided separately if needed
                labels: ['Wallet','Cash on Delivery', 'Online payment'],
                responsive: [{
                    breakpoint: 480,
                    options: {
                        chart: {
                            width: 200
                        },
                        legend: {
                            position: 'bottom'
                        }
                    }
                }]
            };

            var chart = new ApexCharts(document.querySelector("#chart2"), options);
            chart.render();
        })
        .catch(error => {
            console.error('Error:', error);

        });
});
