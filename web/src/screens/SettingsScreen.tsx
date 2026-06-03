import { useState } from 'react'

interface Props {
  countdown: number
  onSave: (val: number) => void
  onBack: () => void
}

export default function SettingsScreen({ countdown, onSave, onBack }: Props) {
  const [value, setValue] = useState(countdown)
  const [error, setError] = useState('')

  function handleSave() {
    if (value < 1 || value > 60) {
      setError('Entrer un nombre entre 1 et 60')
      return
    }
    onSave(value)
  }

  return (
    <div className="screen settings-screen">
      <h2 className="screen-title">Paramètres ⚙️</h2>

      <div className="settings-block">
        <label className="settings-label" htmlFor="countdown-input">
          Compte à rebours (secondes)
        </label>
        <input
          id="countdown-input"
          className="settings-input"
          type="number"
          min={1}
          max={60}
          value={value}
          onChange={e => {
            setValue(Number(e.target.value))
            setError('')
          }}
        />
        {error && <p className="settings-error">{error}</p>}
      </div>

      <button className="send-btn" onClick={handleSave}>
        SAUVEGARDER ✅
      </button>

      <button className="back-btn" onClick={onBack}>← Retour</button>
    </div>
  )
}
