/*
 * Copyright (c) 2026 Boi Stuaud
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.boistuaud.axfr4hyperscalerdns;

/**
 * Entry point class for Axfr Hyperscaler DNS project.
 *
 * <p>This application provides a secondary DNS Server that accepts Zone Transfers (AXFR, IXFR) and
 * updates a Hyperscaler DNS Service, or acts as a primary DNS server detecting changes in
 * Hyperscaler DNS Services Zones and transmitting zone transfers to secondary servers.
 */
public class AxfrHyperscalerDns {

  /**
   * Main entry point for the application.
   *
   * <p>This method serves as the starting point for the DNS server application. Future
   * implementation will include command-line argument parsing, configuration loading, and DNS
   * server initialization logic.
   *
   * @param args Command-line arguments (optional)
   */
  public static void main(String[] args) {
    System.out.println("Axfr Hyperscaler DNS Application Started");
    System.out.println("Version: 1.0.0-SNAPSHOT");
    System.out.println("Java Version: " + System.getProperty("java.version"));

    // Example of accepting command-line arguments (can be implemented later)
    if (args.length > 0) {
      System.out.println("Arguments received: " + java.util.Arrays.toString(args));
    }

    System.out.println("Ready to serve DNS requests...");
  }
}
