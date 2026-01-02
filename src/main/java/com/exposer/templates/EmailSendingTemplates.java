package com.exposer.templates;

public class EmailSendingTemplates {

    public static String sendVerificationEmail(String name, String verificationLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            color: #333;
                            max-width: 600px;
                            margin: 0 auto;
                            padding: 20px;
                        }
                        .container {
                            background-color: #fff;
                            border: 1px solid #ddd;
                            border-radius: 8px;
                            padding: 30px;
                        }
                        .header {
                            text-align: center;
                            margin-bottom: 30px;
                        }
                        .logo {
                            font-size: 24px;
                            font-weight: bold;
                            color: #4a5568;
                            margin-bottom: 10px;
                        }
                        .content {
                            margin-bottom: 30px;
                        }
                        .greeting {
                            font-size: 18px;
                            margin-bottom: 20px;
                        }
                        .message {
                            margin-bottom: 25px;
                            color: #666;
                        }
                        .verification-button {
                            display: inline-block;
                            background-color: #4299e1;
                            color: white;
                            text-decoration: none;
                            padding: 12px 30px;
                            border-radius: 4px;
                            font-size: 16px;
                            margin: 20px 0;
                        }
                        .note {
                            background-color: #f7fafc;
                            padding: 15px;
                            border-radius: 4px;
                            margin-top: 20px;
                            font-size: 14px;
                            color: #718096;
                        }
                        .footer {
                            text-align: center;
                            margin-top: 30px;
                            padding-top: 20px;
                            border-top: 1px solid #e2e8f0;
                            color: #a0aec0;
                            font-size: 12px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <div class="logo">EXPOSER</div>
                        </div>
                        <div class="content">
                            <div class="greeting">Hello %s,</div>
                            <div class="message">
                                Welcome to Exposer! Please verify your email address to complete your registration.
                            </div>
                            <div style="text-align: center;">
                                <a href="%s" class="verification-button">Verify Email Address</a>
                            </div>
                            <div class="note">
                                This verification link will expire in 24 hours. If you didn't create an account with Exposer, please ignore this email.
                            </div>
                        </div>
                        <div class="footer">
                            © 2025 Exposer. All rights reserved.
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(name, verificationLink);
    }

    public static String sendWelcomeEmail(String name) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            color: #333;
                            max-width: 600px;
                            margin: 0 auto;
                            padding: 20px;
                        }
                        .container {
                            background-color: #fff;
                            border: 1px solid #ddd;
                            border-radius: 8px;
                            padding: 30px;
                        }
                        .header {
                            text-align: center;
                            margin-bottom: 30px;
                        }
                        .logo {
                            font-size: 24px;
                            font-weight: bold;
                            color: #4a5568;
                            margin-bottom: 10px;
                        }
                        .welcome-title {
                            text-align: center;
                            color: #2d3748;
                            margin-bottom: 20px;
                        }
                        .content {
                            margin-bottom: 30px;
                        }
                        .greeting {
                            font-size: 18px;
                            margin-bottom: 20px;
                        }
                        .message {
                            margin-bottom: 25px;
                            color: #666;
                        }
                        .features {
                            margin: 30px 0;
                        }
                        .feature {
                            background-color: #f8fafc;
                            padding: 15px;
                            margin: 10px 0;
                            border-radius: 4px;
                            border-left: 3px solid #4299e1;
                        }
                        .footer {
                            text-align: center;
                            margin-top: 30px;
                            padding-top: 20px;
                            border-top: 1px solid #e2e8f0;
                            color: #a0aec0;
                            font-size: 12px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <div class="logo">EXPOSER</div>
                        </div>
                        <div class="content">
                            <h2 class="welcome-title">Welcome to Exposer!</h2>
                            <div class="greeting">Hello %s,</div>
                            <div class="message">
                                We're excited to have you join our community. Get ready to explore amazing features!
                            </div>
                            <div class="features">
                                <div class="feature">
                                    <strong>Explore Features:</strong> Discover all the tools we have to offer
                                </div>
                                <div class="feature">
                                    <strong>Connect:</strong> Join our growing community
                                </div>
                                <div class="feature">
                                    <strong>Get Started:</strong> Begin your journey with us today
                                </div>
                            </div>
                        </div>
                        <div class="footer">
                            © 2025 Exposer. All rights reserved.
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(name);
    }
}