# TON Sign GUI

[EN](README.md) | [日本語](README-JP.md)

## Still very in development, will have big changes all the time.

## Original Idea By [\~Emerald~](https://github.com/Emmyvee/TON-Sign)

**TON Sign GUI** is a complete rework GUI version of the [**TON Sign**](https://github.com/Emmyvee/TON-Sign).
This asset uses OSC to display the next
round type besides the start button, meaning you should never miss another Alternate round again!

<div style="display: flex; justify-content: space-around;">
  <img src="https://raw.githubusercontent.com/Emmyvee/TON-Sign/refs/heads/main/VRCPreview.jpg" alt="Thanks to the people who helped me test!" width="350"/>
  <img src="https://raw.githubusercontent.com/Emmyvee/TON-Sign/refs/heads/main/VRCPreview2.jpg" alt="Preview Photo" width="350"/>
</div>

<div style="display: flex; justify-content: space-around;">
  <img src="https://raw.githubusercontent.com/T2PeNBiX99wcoxKv3A4g/TON-Sign/refs/heads/main/Screenshot/VRCPreview_JP.png" alt="Preview Photo Japanese" width="350"/>
</div>

# [Download](https://github.com/T2PeNBiX99wcoxKv3A4g/TON-Sign-GUI/releases/latest)

## Setup Instructions

1. Download [Modular Avatar](https://modular-avatar.nadena.dev/) or [VRCFury](https://vrcfury.com)
2. Drag the `TON Constraint MA` or `TON Constraint VRCFury` Prefab onto your Avatar's Root.
    - This should be at the same hierarchy level as `Body` `Armature` or similar objects.
3. Change `Hand` transform to your avatar right-hand transform as you want, not very important unless you want to pick
   up using your hand

### Usage Notes

- Processing Time:
    - The app may need to process up to two rounds to determine your lobby's round order!
    - On a completely fresh lobby, this may be slightly longer due to `level of suffering` but it should correct itself
      fast!
    - The app can't accurately determine the next round is, because Terror of nowhere getting update makes the next
      round
      complete random, so sometime will add `maybe` tag if not sure.
- Existing Lobby:
    - If you’ve already been in the lobby for a short while before launching the app, it should automatically detect the
      round order without needing to process any new rounds. Just start the app and toggle the sign!

### Running the App

1. Launch `TON-Sign-(version).exe` or `TON-Sign-(version).msi` if you want to install to your system!
2. Start some rounds in Terrors of Nowhere.
    - The app will handle the rest!

---

### Credits

- [\~Emerald\~](https://github.com/Emmyvee/TON-Sign) - First creator
- [hoijui](https://github.com/hoijui/JavaOSC) - JavaOSC
- [olk90](https://github.com/olk90/compose-tableView) - Table View
