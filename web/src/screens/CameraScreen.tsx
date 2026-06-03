import { useRef } from 'react'

interface Props {
  onPhoto: (dataUrl: string, file: File) => void
  onSettings: () => void
}

export default function CameraScreen({ onPhoto, onSettings }: Props) {
  const inputRef = useRef<HTMLInputElement>(null)

  function handleFile(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0]
    if (!file) return
    const reader = new FileReader()
    reader.onload = () => onPhoto(reader.result as string, file)
    reader.readAsDataURL(file)
    // Reset input so same photo can be retaken
    e.target.value = ''
  }

  return (
    <div className="screen camera-screen">
      <h1 className="app-title">PhotoShare</h1>

      <button
        className="camera-btn"
        onClick={() => inputRef.current?.click()}
        aria-label="Prendre une photo"
      >
        📷
      </button>

      <input
        ref={inputRef}
        type="file"
        accept="image/*"
        capture="environment"
        onChange={handleFile}
        style={{ display: 'none' }}
      />

      <button
        className="settings-fab"
        onClick={onSettings}
        aria-label="Paramètres"
      >
        ⚙️
      </button>
    </div>
  )
}
