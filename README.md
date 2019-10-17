## 明日方舟游客帐号管理APP - 粥游存档

> 这个APP可以从明日方舟本地存档（`/data/data/com.hypergryph.arknights/`或其他目录）中提取游客帐号token并进行管理
1. 设置不同的存档未知（APP多开）
2. 将不同存档中的游客帐号提取（或者手动添加）并存储在APP的私有目录中
3. 在不同的存档中应用不同的游客帐号

> 提取、应用游客帐号时需要root权限（操作存档前将存档权限设置为可读或可读写，操作完毕后将读写权限去除，防止其他恶意软件操作）
> 
> 为了帐号安全性，APP暂时不支持已管理的游客帐号的导入导出操作，如需备份，可以使用有root权限的文件浏览器，手动备份`/data/data/org.lintx.akprefsmanage/app_data/data.json`文件

#### 手动添加游客帐号token
> 此功能用于在不同的设备间导入、导出帐号
1. 打开一个有root权限的文件浏览器
2. 定位到明日方舟存档文件（默认为`/data/data/com.hypergryph.arknights/shared_prefs/com.hypergryph.arknights.v2.playerprefs.xml`文件
3. 使用编辑器或文本浏览器打开存档文件，并从中找到类似`<string name="LOGIN_SDK_TOKEN_GUEST">abcd</string>`的内容，两个尖括号中间（例子中为`abcd`）的就是存档游客帐号的token
4. 复制token，并手动添加到APP中即可
> 各手机、模拟器多开的存档位置不一，mumu模拟器多开的存档位置为`/data/user/<10/11/12/13>/com.hypergryph.arknights/shared_prefs/com.hypergryph.arknights.v2.playerprefs.xml`，<10/11/12/13>分别是#N1、#N2、#N3、#N4的存档