import React, { Component } from 'react'

import WeekReset from './Buttons/WeekReset'
import WeekCopy from './Buttons/WeekCopy'
import WeekLock from './Buttons/WeekLock'
import WeekSave from './Buttons/WeekSave'
import WeekMinimize from './Buttons/WeekMinimize'
import WeekNavigate from './Buttons/WeekNavigate'

import { Container, Row, Col } from 'react-bootstrap'

export default class WeekButtons extends Component {
    render() {
        return (
            <div>
                <Row>
                    <h3>Ukesknapper</h3>
                </Row>
                
                <WeekReset />
                <br />
                <WeekCopy />
                <br />
                <WeekLock />
                <br />
                <WeekSave />
                <br />
                <WeekMinimize />
                <br />
                <WeekNavigate />
            </div>
        )
    }
}
