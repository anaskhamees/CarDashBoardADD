import time

# Function to simulate temperature values
def simulate_temperature(filename):
    # Step 0: Open the file in write mode to clear all its content (empty the file)
    with open(filename, 'w') as file:
        pass  # Opening the file in 'w' mode clears it

    # Step 1: Wait for 11 seconds before starting
    print("Waiting for 13 seconds...")
    time.sleep(13)

    # Step 2: Open the file again to write the temperature values
    with open(filename, 'w') as file:
        # Step 3: Gradually increase temperature from 0 to 30 over 2 seconds
        print("Increasing temperature from 0 to 30...")
        temperature = 0
        while temperature <= 30:
            file.write(f"{temperature}\n")  # Write only the value
            temperature += 1
            time.sleep(2 / 30)  # Spread the 30 increments over 2 seconds
        
        # Step 4: Stay at 30 for 2 seconds
        print("Staying at temperature 30 for 2 seconds...")
        for _ in range(int(2 / 0.25)):  # Write temperature every 0.25 seconds for 2 seconds
            file.write(f"30\n")  # Write only the value
            time.sleep(0.25)

        # Step 5: Gradually increase temperature to 50 over 2 seconds
        print("Increasing temperature from 30 to 50...")
        while temperature <= 50:
            file.write(f"{temperature}\n")  # Write only the value
            temperature += 1
            time.sleep(2 / 20)  # Spread the 20 increments over 2 seconds

# Call the function to simulate temperature and write to the file
simulate_temperature("temps.txt")

