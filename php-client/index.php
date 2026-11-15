<?php
// PHP 8+ with cURL extension. Start: php -S localhost:8000 -t php-client
$result = ''; $error = '';
$text = $_POST['text'] ?? '';
$language = $_POST['language'] ?? 'English';
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (trim($text) === '') { $error = 'Please enter text.'; }
    else {
        $url = getenv('TRANSLATOR_URL') ?: 'http://localhost:8080/darija-translator/api/translate';
        $user = getenv('TRANSLATOR_USER') ?: 'translator';
        $password = getenv('TRANSLATOR_PASSWORD') ?: '';
        if ($password === '') { $error = 'Configure TRANSLATOR_PASSWORD on the PHP server.'; }
        else {
            $handle = curl_init($url);
            curl_setopt_array($handle, [CURLOPT_RETURNTRANSFER => true, CURLOPT_POST => true,
                CURLOPT_POSTFIELDS => json_encode(['text' => $text, 'sourceLanguage' => $language]),
                CURLOPT_HTTPHEADER => ['Content-Type: application/json'], CURLOPT_USERPWD => "$user:$password",
                CURLOPT_TIMEOUT => 45]);
            $body = curl_exec($handle);
            $status = curl_getinfo($handle, CURLINFO_HTTP_CODE);
            $curlError = curl_error($handle);
            curl_close($handle);
            $data = json_decode($body ?: '{}', true);
            if ($status === 200 && isset($data['translation'])) $result = $data['translation'];
            else $error = $data['error'] ?? ($curlError ?: "API returned HTTP $status");
        }
    }
}
function h($s) { return htmlspecialchars((string)$s, ENT_QUOTES | ENT_SUBSTITUTE, 'UTF-8'); }
?>
<!doctype html><html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Darija Translator PHP Client</title><style>body{font:16px system-ui;max-width:640px;margin:40px auto;padding:20px;background:#f6faf8}textarea,input{width:100%;padding:10px;box-sizing:border-box;margin:8px 0 16px}button{padding:12px 24px;background:#146a48;color:white;border:0;border-radius:6px}.error{color:#b00020}</style></head><body><h1>🇲🇦 Darija Translator</h1><form method="post"><label>Source language<input name="language" value="<?=h($language)?>"></label><label>Text<textarea name="text" rows="6" required><?=h($text)?></textarea></label><button>Translate</button></form><?php if ($error): ?><p class="error"><?=h($error)?></p><?php endif; ?><?php if ($result): ?><h2>Translation</h2><p dir="auto"><?=nl2br(h($result))?></p><?php endif; ?></body></html>
