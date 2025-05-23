# TON Sign GUI

[EN](README.md) | [日本語](README-JP.md)

## まだ開発中ので、常に大きな変化があるかもしれません。

## 元ネタは[\~Emerald~](https://github.com/Emmyvee/TON-Sign)さんです

**TON Sign GUI**は[**TON Sign**](https://github.com/Emmyvee/TON-Sign)を参照して最初から作ってたのGUIバージョンです  
このソフトはOSCを使って次のラウンドを表示するので使たら次のラウンドをミスするのことは絶対ない！

<div style="display: flex; justify-content: space-around;">
  <img src="https://raw.githubusercontent.com/Emmyvee/TON-Sign/refs/heads/main/VRCPreview.jpg" alt="Thanks to the people who helped me test!" width="350"/>
  <img src="https://raw.githubusercontent.com/Emmyvee/TON-Sign/refs/heads/main/VRCPreview2.jpg" alt="Preview Photo" width="350"/>
</div>

<div style="display: flex; justify-content: space-around;">
  <img src="https://raw.githubusercontent.com/T2PeNBiX99wcoxKv3A4g/TON-Sign/refs/heads/main/Screenshot/VRCPreview_JP.png" alt="Preview Photo Japanese" width="350"/>
</div>

# [ダウンロード](https://github.com/T2PeNBiX99wcoxKv3A4g/TON-Sign-GUI/releases/latest)

## セットアップ方法

1. [Modular Avatar](https://modular-avatar.nadena.dev/)または[VRCFury](https://vrcfury.com)を事前に導入してください。
2. `TON Constraint MA JP`または`TON Constraint VRCFury`プリファブをアバタのルートにドラッグとしてください。
    - これは、`Body`や`Armature`などのオブジェクトと同じ階層レベルです。
3. 絶対に必要はないけどもし右手で看板を持ってたいならば`Hand`トランスフォームをアバターの右手トランスフォームに変更してください。

### 使用上の注意

- 処理時間:
    - このアプリは、ロビーのラウンド順を決定するために、最大2つのラウンドを処理する必要がある場合があります！
    - 完全に新しいなロビーでは、`level of suffering`（`苦しみのレベル`）のために多少時間がかかるかもしれませんがすぐに直るはずです。
    - とはいえ、このアプリはTONがラウンドを完全にランダムになるの更新をしてたの原因で
      次のラウンドを正確に予測するができないです。  
      そのため、確信が持てない場合は`たぶん`タグを追加することになる。
- 既存ロビー:
    - アプリを起動する前にすでにロビーにしばらくいた場合、新しいラウンドを処理することではなく、
      自動的にラウンド順を検出するはずです  
      だから看板を切り替えるだけでいい！

### アプリの実行

1. `TON-Sign-(バージョン).exe`を起動してください  
   もしシステムにインストールするしたいの場合は`TON-Sign-(バージョン).msi`を起動してください
2. TONのインスタンスにジョインして、ソフトが自動的に処理するはずです

---

### クレジット

- [\~Emerald\~](https://github.com/Emmyvee/TON-Sign) - 最初のクリエイター
- [hoijui](https://github.com/hoijui/JavaOSC) - JavaOSC
- [olk90](https://github.com/olk90/compose-tableView) - Table View