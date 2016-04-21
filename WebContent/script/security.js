// 屏蔽掉IE控制键，保证安全性
document.onkeydown = disableKeys;

function disableKeys() {
	if (event.keyCode == 116) {		// F5 刷新
		window.event.keyCode = 0;
		return false;
	}
	
	if (event.keyCode == 8) {		// BackSpace 退格
		var obj = event.target || event.srcElement;
		var type = obj.type || obj.getAttribute("type");
		if (type == 'text' || type == 'password' || type == 'textarea') {
			// 在输入框里点了退格
			if (obj.getAttribute("readonly") == true) {
				// 在只读的输入框里点退格，屏蔽
				event.keyCode = 0;
				return false;
			}
		} else {
			// 在非输入框里点退格，屏蔽
			event.keyCode = 0;
			return false;
		}
	}
}