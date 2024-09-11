import os
import socket
import RPi.GPIO as GPIO
import time

# Configuration
LED_PIN = 17  # GPIO pin where the LED is connected

# Set up GPIO
GPIO.setmode(GPIO.BCM)
GPIO.setup(LED_PIN, GPIO.OUT)

def get_ip_address():
    """Get the IP address of the Raspberry Pi."""
    try:
        hostname = socket.gethostname()
        ip_address = socket.gethostbyname(hostname)
        return ip_address
    except socket.error as e:
        print(f"Error retrieving IP address: {e}")
        return None

def ping(ip):
    """Ping the given IP address and return True if reachable."""
    response = os.system(f"ping -c 1 {ip}")
    return response == 0

try:
    # Get the local IP address of the Raspberry Pi
    pi_ip = get_ip_address()
    if pi_ip is None:
        raise ValueError("Could not determine IP address. Exiting.")
    
    print(f"Detected IP Address: {pi_ip}")

    while True:
        if ping(pi_ip):
            GPIO.output(LED_PIN, GPIO.HIGH)  # Turn on the LED
        else:
            GPIO.output(LED_PIN, GPIO.LOW)   # Turn off the LED
        time.sleep(5)  # Wait for 5 seconds before checking again

except KeyboardInterrupt:
    print("Program interrupted by user")

finally:
    GPIO.cleanup()  # Clean up GPIO on exit
