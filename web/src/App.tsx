import { useState } from 'react'
import type { Recipient, Selection } from './types'
import { DEFAULT_RECIPIENTS } from './types'
import CameraScreen from './screens/CameraScreen'
import RecipientsScreen from './screens/RecipientsScreen'
import CountdownScreen from './screens/CountdownScreen'
import SettingsScreen from './screens/SettingsScreen'

type Screen = 'camera' | 'recipients' | 'countdown' | 'settings'

export default function App() {
  const [screen, setScreen] = useState<Screen>('camera')
  const [photo, setPhoto] = useState<string | null>(null)
  const [photoFile, setPhotoFile] = useState<File | null>(null)
  const [selections, setSelections] = useState<Selection[]>([])
  const [countdown, setCountdown] = useState(5)
  const [recipients] = useState<Recipient[]>(DEFAULT_RECIPIENTS)

  return (
    <div className="app">
      {screen === 'camera' && (
        <CameraScreen
          onPhoto={(dataUrl, file) => {
            setPhoto(dataUrl)
            setPhotoFile(file)
            setScreen('recipients')
          }}
          onSettings={() => setScreen('settings')}
        />
      )}

      {screen === 'recipients' && (
        <RecipientsScreen
          recipients={recipients}
          onSend={(sels) => {
            setSelections(sels)
            setScreen('countdown')
          }}
          onBack={() => setScreen('camera')}
        />
      )}

      {screen === 'countdown' && photo && (
        <CountdownScreen
          photo={photo}
          photoFile={photoFile}
          selections={selections}
          countdownSeconds={countdown}
          onCancel={() => setScreen('recipients')}
          onDone={() => {
            setPhoto(null)
            setPhotoFile(null)
            setSelections([])
            setScreen('camera')
          }}
        />
      )}

      {screen === 'settings' && (
        <SettingsScreen
          countdown={countdown}
          onSave={(val) => {
            setCountdown(val)
            setScreen('camera')
          }}
          onBack={() => setScreen('camera')}
        />
      )}
    </div>
  )
}
