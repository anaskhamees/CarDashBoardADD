## ADS1115 and ACS712 Current Measurement on Raspberry Pi

This project demonstrates how to measure current using the ADS1115 analog-to-digital converter (ADC) and the ACS712 current sensor on a Raspberry Pi. The ADC readings are processed to calculate the current flowing through the ACS712 sensor, and the results are saved to a text file.

### Hardware Setup
- Raspberry Pi: The central unit controlling the ADC and processing data.
- ADS1115: 16-bit ADC module connected to the Raspberry Pi via I2C.
- ACS712: Current sensor that outputs a voltage proportional to the current flowing through it.
- I2C Bus: The communication interface between the Raspberry Pi and the ADS1115.
Wiring Diagram

### ADS1115 Connections:

- VDD to Raspberry Pi 3.3V
- GND to Raspberry Pi GND
- SCL to Raspberry Pi SCL (GPIO 3)
- SDA to Raspberry Pi SDA (GPIO 2)
- AIN0 to ACS712 Output
 
### ACS712 Connections:
- VCC to 5V power supply
- GND to Ground
- OUT to ADS1115 AIN0

## Code Explanation
1. Initializing I2C and Configuring ADS1115
The script initializes the I2C bus and configures the ADS1115 to operate in single-ended mode, with a ±4.096V input range and a data rate of 128 samples per second. It also sets the input channel to AIN0, where the ACS712 sensor is connected.

2. Reading ADC Values
After configuring the ADS1115, the script reads the conversion results from the ADC. The byte order of the result is swapped to match the format expected by the Raspberry Pi.

3. Converting ADC Reading to Current
The ADC value is converted to a voltage using the reference voltage and ADC resolution. This voltage is then converted to a current value based on the sensitivity of the ACS712 sensor.

4. Saving the Results
The calculated current value is saved to a text file (current.txt) for logging or further analysis.