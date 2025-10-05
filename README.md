# Encrypt-O-Nator

![dr_heinz](https://github.com/user-attachments/assets/3182f002-f608-4f4e-acbe-72f876621f64)

A modified fork of Android Keyboard - [HeliBoard](https://github.com/Helium314/HeliBoard) with integrated end-to-end encryption capabilities. Type, encrypt, and decrypt messages directly from your keyboard without app switching.

## Overview

Encrypt-O-Nator combines a fully-functional Android keyboard with Signal Protocol encryption, allowing seamless secure communication through any messaging app. It works in tandem with the [Crypto Middleware](https://github.com/stefanmrvic/crypto-middleware) app for key exchange and contact management.

## Features

- **Hidden encryption controls** - Three discreet buttons integrated into the keyboard toolbar
- **One-tap encryption** - Encrypt typed text instantly with selected contact's key
- **Clipboard decryption** - Decrypt copied messages with automatic sender identification
- **Contact selector** - Quick dropdown to switch between encryption recipients
- **Zero app switching** - Everything happens within the keyboard interface

## Workflow

### Initial Setup

1. **Install both apps:**
   - Crypto Middleware (for key management)
   - Encrypt-O-Nator (this keyboard)

2. **Exchange keys via Middleware:**
   - Open Crypto Middleware
   - Use QR code exchange to establish sessions with contacts
   - Sessions are shared between both apps via ContentProvider

3. **Enable keyboard:**
   - Go to Android Settings → System → Languages & Input
   - Enable Encrypt-O-Nator keyboard
   - Grant "Display over other apps" permission for contact selector

### Daily Use

#### Sending Encrypted Messages

1. Open any messaging app (WhatsApp, Signal, Telegram, SMS, etc.)
2. Type your message normally
3. Tap **SELECT_WORD button** → Select recipient contact
4. Tap **UNDO button** → Message encrypts and copies to clipboard
5. Paste and send via your messaging app

#### Receiving Encrypted Messages

1. Copy the encrypted message you received
2. In any text field, tap **REDO button**
3. Popup displays decrypted message and sender name
4. Tap "Copy" to copy plaintext, or dismiss popup

### Button Functions

| Button | Original Function | Crypto Function |
|--------|------------------|-----------------|
| **SELECT_WORD** | Select word | **Contact Selector** - Choose encryption recipient |
| **UNDO** | Undo typing | **Encrypt** - Encrypt text with selected contact's key |
| **REDO** | Redo typing | **Decrypt** - Decrypt clipboard content, show popup |

## Technical Stack

### Encryption
- **Signal Protocol** (libsignal-protocol-java) - Double Ratchet algorithm with forward secrecy
- **X3DH Key Agreement** - Extended Triple Diffie-Hellman for initial key exchange
- **AES-256-CBC** - Symmetric encryption for message payloads

### Android Components
- **ContentProvider** - IPC bridge between keyboard and middleware app
- **EncryptedSharedPreferences** - Secure local storage for session data
- **WindowManager Overlays** - Contact selector and decrypt popup UI

### Keyboard Base
- Fork of [HeliBoard](https://github.com/Helium314/HeliBoard) - Privacy-focused AOSP keyboard
- Maintains all original HeliBoard features and customization options

## Architecture
┌─────────────────┐         ┌──────────────────┐
│ Encrypt-O-Nator │◄────────┤ Crypto Middleware│
│   (Keyboard)    │  IPC    │   (Key Manager)  │
└────────┬────────┘         └──────────────────┘
│                           │
├─ CryptoHelper             ├─ SignalProtocolManager
├─ ContactSelectorOverlay   ├─ QR Code Scanner/Generator
└─ InputLogic (modified)    └─ Session Storage

**Data Flow:**
1. Middleware manages key exchange and contact storage
2. CryptoProvider exposes encryption/decryption via ContentProvider
3. Keyboard queries contacts, encrypts/decrypts via IPC calls
4. No sensitive data stored in keyboard - all keys in Middleware

## Security Considerations

- **Forward secrecy** - Each message uses unique keys, deleted after use
- **Deniability** - No cryptographic proof of message authorship
- **No cloud backup** - Keys stored locally only, never synced
- **One-time decryption** - Messages can only be decrypted once (ratchet advancement)
- **Isolated storage** - Keyboard and middleware use separate secure storage

## Installation

### Building from Source
```bash
# Clone repository
git clone https://github.com/yourusername/encrypt-o-nator.git
cd encrypt-o-nator

# Build
./gradlew assembleRelease

# Install both APKs
adb install app/build/outputs/apk/release/encrypt-o-nator.apk
adb install middleware/build/outputs/apk/release/cryptomiddleware.apk
Permissions Required

Display over other apps - Contact selector overlay and decrypt popup
Read clipboard - Decrypt copied messages

Compatibility

Android 8.0+ (API 26+)
Works with any messaging app that accepts text input
HeliBoard features fully preserved (swipe typing, themes, etc.)

Credits

Based on HeliBoard keyboard
Uses libsignal-protocol-java for encryption
Inspired by the need for friction-free E2EE in everyday messaging

License
[Your chosen license - must be compatible with HeliBoard's GPL-3.0]

Note: This keyboard provides transport-layer encryption. For maximum security, use apps with built-in E2EE (Signal, WhatsApp) alongside this tool for defense in depth.
