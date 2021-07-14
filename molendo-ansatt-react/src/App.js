import React, { Component } from 'react'
import { BrowserRouter as Router, Route, Switch, Redirect } from 'react-router-dom'
import i18n from './Language/i18n'
import Toolbar from './Toolbar/Toolbar'
import Calendar from './Calendar/Calendar'
import Login from './Login/Login'
import Information from './Information/Information'
import Home from './Home/Home'
import { loadAuth, loadService } from './Jwt/Jwt'
import './App.css'
import 'bootstrap/dist/css/bootstrap.min.css'
import './assets/css/magic-circle.css'
import './assets/css/data-svar.css'


export default class App extends Component {
	constructor(props) {
		super(props)

		this.state = {
			services: null,
			baseUrl: "http://18.191.107.144:88/molendo/",
			authenticated: loadAuth(),
			service: loadService(),
			currentLanguage: i18n.language,
			rosters: [],
		}

		this.setAuthenticated = this.setAuthenticated.bind(this)
		this.setRosters = this.setRosters.bind(this)
		this.setServices = this.setServices.bind(this)
		this.deleteRoster = this.deleteRoster.bind(this)
		this.addRoster = this.addRoster.bind(this)
		this.createDate = this.createDate.bind(this)
		this.saveRosters = this.saveRosters.bind(this)
		this.getRosters = this.getRosters.bind(this)
		this.replaceRostersOnDate = this.replaceRostersOnDate.bind(this)
		this.replaceRostersOnWeek = this.replaceRostersOnWeek.bind(this)
		this.deleteRostersBetweenDates = this.deleteRostersBetweenDates.bind(this)
		this.formatDate = this.formatDate.bind(this)
		this.formatTime = this.formatTime.bind(this)
		this.handleLangChange = this.handleLangChange.bind(this)
		this.logOut = this.logOut.bind(this)
		this.onLanguageChanged = this.onLanguageChanged.bind(this)
		this.handleServiceChange = this.handleServiceChange.bind(this)
	}

	// Necessary?
	componentWillUnmount() {
		i18n.off('languageChanged', this.onLanguageChanged)
	}

	// Observer method for i18next
	onLanguageChanged(lang) {
		this.forceUpdate()
	}
	
	componentDidMount() {
		i18n.on('languageChanged', this.onLanguageChanged)
		if (this.state.authenticated && this.state.rosters.length === 0)
			this.getRosters(this.state.authenticated.opServId,
				this.state.authenticated.jwt)
		const lang = localStorage.getItem('MolendoLang')
		if (lang) i18n.changeLanguage(lang)
	}

	// Receives service name and changes related state variables
	// used to design theme and other things
	handleServiceChange(serviceName) {
		const service = this.state.services[serviceName]
		this.setState({service: {
			name: serviceName,
			theme: service["fldServCssTheme"],
			minHour: service["fldServMinHour"],
			maxHour: service["fldServMaxHour"]}
		})
	}


	logOut() {
		localStorage.removeItem('MolendoToken')
		this.setState({authenticated: null, rosters: []})
	}


	handleLangChange(lang) {
		this.setState({currentLanguage: lang})
		i18n.changeLanguage(lang)
		localStorage.setItem('MolendoLang', lang)
		document.documentElement.lang = lang
	}


	setAuthenticated(auth) { 
		this.setState({authenticated: auth})
		localStorage.setItem('MolendoToken', auth.jwt)
	}


	setRosters(data) {
		this.setState(() => {
			let sortedRosters = data.sort((a, b) => {
				let aStart = this.createDate(a.fldOpRStartDate, a.fldOpRStartTime);
				let bStart = this.createDate(b.fldOpRStartDate, b.fldOpRStartTime);
				return aStart - bStart
			})
			return {
				rosters: sortedRosters
			}
		})
	}


	setServices(data) {
		const services = {}
		data.forEach(service => services[service.fldServID] = service)
		this.setState({services: services})
	}


    getRosters(opServId, jwt) {
		if (!opServId && !jwt) {
			opServId = this.state.authenticated.opServId
			jwt = this.state.authenticated.jwt
		}
		let url = this.state.baseUrl+"roster/"+opServId
		let options =  {
			mode: 'cors',
			headers: {
				'Access-Control-Allow-Origin': '*',
				'Authorization': 'Bearer '+jwt
			}
		}
		
        fetch(url, options)
                .then(async response => {
                    const data = await response.json()
                    if (response.status == 200) {
                        this.setRosters(data)
						return
						/*
                    } else if (response.status === 400 || response.status === 401) {
						this.logOut()*/
                    } else {
                        const error = (data && data.message) || response.status
						this.logOut()
                        return Promise.reject(error)
                    }
                })
                .catch(error => {
					this.logOut()
                    console.log(error)
                })
    }


	// Helper method for comparing datetimes
	createDate(date, time) {
		date = new Date(date)
		time = new Date(time)
		date.setHours(time.getHours())
		date.setMinutes(time.getMinutes())
		date.setSeconds(time.getSeconds())
		return date
	}


	// Helper method for formatting DB date
	formatDate(dateTime) {
		let time = "T00:00:00"
		let month = dateTime.getMonth() + 1 > 9 ?
			dateTime.getMonth() + 1 : "0" + (dateTime.getMonth() + 1)
		let date = dateTime.getDate() > 9 ?
			dateTime.getDate() : "0" + dateTime.getDate()
		return dateTime.getFullYear() + "-" + month + "-" + date + time
	}


	// Helper method for formatting DB time
	formatTime(dateTime) {
		let date = "1900-01-01T"
		let hours = dateTime.getHours() > 9 ?
			dateTime.getHours() : "0" + dateTime.getHours()
		let minutes = dateTime.getMinutes() > 9 ?
			dateTime.getMinutes() : "0" + dateTime.getMinutes()
		let seconds = dateTime.getSeconds() > 9 ?
			dateTime.getSeconds() : "0" + dateTime.getSeconds()
		return date + hours + ":" + minutes + ":" + seconds
	}


	addRoster(start, end, openEnded, shouldSave) {
		let startDate = this.formatDate(start)
		let endDate
		if (openEnded) endDate = startDate
		else endDate = this.formatDate(end)
		let startTime = this.formatTime(start)
		let endTime = openEnded ? null : this.formatTime(end)
		this.setState(prevState => {
			let newRosters = prevState.rosters

			let newRoster = {
				fldOpServID: prevState.authenticated.opServId,
				fldOpRStartDate: startDate,
				fldOpRStartTime: startTime,
				fldOpREndDate: endDate,
				fldOpREndTime: endTime,
				fldOpRLogOn: false,
				fldOpRLogOut: false,
				fldOpRDropIn: false,
				fldOpROnHold: false,
				fldOpRComment: ""
			}

			// Maintaining sorting order in roster list when adding new
			for (let i = 0; i < prevState.rosters.length; i++) {
				let pointerStart = new Date(prevState.rosters[i].fldOpRStartDate)

				let pointerStartTime = new Date(prevState.rosters[i].fldOpRStartTime)
				pointerStart.setHours(pointerStartTime.getHours())
				pointerStart.setMinutes(pointerStartTime.getMinutes())
				pointerStart.setSeconds(pointerStartTime.getSeconds())

				if (start.getTime() < pointerStart.getTime()) {
					newRosters.splice(i, 0, newRoster)
					break
				} else if (i === prevState.rosters.length - 1) {
					newRosters.push(newRoster)
					break
				}
			}
			if (!newRosters.length) newRosters.push(newRoster)
			return {
				rosters: newRosters
			}
		}, () => {
			if (shouldSave) {
				this.saveRosters().then(async response => {
					if (response.status == 201 || response.status == 200) 
						this.getRosters()
				})
			}
		})
	}


	// Deletes roster with given start time
	deleteRoster(start, shouldSave) {
		let deleteTime = new Date(start)
		this.setState(prevState => {
			let newRosters = prevState.rosters

			for(let i = 0; i < newRosters.length; i++) {
				let rosterStart = new Date(newRosters[i].fldOpRStartDate)
				let rosterStartTime = new Date(newRosters[i].fldOpRStartTime)
				rosterStart.setHours(rosterStartTime.getHours())
				rosterStart.setMinutes(rosterStartTime.getMinutes())
				rosterStart.setSeconds(rosterStartTime.getSeconds())
				rosterStart.setMilliseconds(rosterStartTime.getMilliseconds())

				if(deleteTime.getTime() === rosterStart.getTime()) {
					if(newRosters[i].fldOpRLogOn === true) newRosters[i].fldOpRLogOut = true
					else newRosters.splice(i, 1)
					break
				}
			}

			return {
				rosters: newRosters
			}
		}, () => {
			if (shouldSave) this.saveRosters()
		})
	}


	replaceRostersOnDate(date, rosters, serverTime) {
		let prevRosters = this.state.rosters
		let currentMinTime = serverTime
		let endTime = new Date(date)

		while (endTime.getHours() != this.state.service.maxHour) {
			endTime.setHours(endTime.getHours() + 1)
		}

		// Delete old rosters on day
		for(let i = 0; i < prevRosters.length; i++) {
			// Skip if shift is finished
			if(prevRosters[i].fldOpRLogOn === true &&
				prevRosters[i].fldOpRLogOut === true) continue

			let rosterStart = new Date(prevRosters[i].fldOpRStartDate)
			let rosterStartTime = new Date(prevRosters[i].fldOpRStartTime)
			rosterStart.setHours(rosterStartTime.getHours())
			rosterStart.setMinutes(rosterStartTime.getMinutes())
			rosterStart.setSeconds(rosterStartTime.getSeconds())

			// Ignore shifts that started before start of day, unless they are ongoing
			if(rosterStart.getTime() < date) continue

			// Exit out of loop if start time of shift is after max time of day
			if(rosterStart.getTime() > endTime.getTime()) continue

			// Delete/end roster if it has not started yet, or if its an openended shift
			if((prevRosters[i].fldOpRLogOn === false && prevRosters[i].fldOpRLogOut === false) 
				|| prevRosters[i].fldOpREndTime === null) this.deleteRoster(rosterStart)
			// Set currentMinTime (used when inserting new rosters) to the end of current ongoing shift
			else {
				let rosterEnd = new Date(prevRosters[i].fldOpREndDate)
				let rosterEndTime = new Date(prevRosters[i].fldOpREndTime)
				
				rosterEnd.setHours(rosterEndTime.getHours())
				rosterEnd.setMinutes(rosterEndTime.getMinutes())
				rosterEnd.setSeconds(rosterEndTime.getSeconds())

				currentMinTime = rosterEnd
				currentMinTime.setMinutes(currentMinTime.getMinutes() + 1)
			}
		}


		// Insert new rosters on day
		rosters.forEach(roster => {
			let start = new Date(date)
			let startTime = new Date(roster.fldOpRStartTime)
			start.setHours(startTime.getHours())
			start.setMinutes(startTime.getMinutes())
			start.setSeconds(startTime.getSeconds())

			let end = new Date(date)
			let endTime = new Date(roster.fldOpREndTime)
			end.setHours(endTime.getHours())
			end.setMinutes(endTime.getMinutes())
			end.setSeconds(endTime.getSeconds())

			if (this.state.service.maxHour < this.state.service.minHour) {
				if (start.getHours() < this.state.service.maxHour) start.setDate(start.getDate() + 1)
				if (end.getHours() < this.state.service.maxHour) end.setDate(end.getDate() + 1)
			}

			if (currentMinTime > start.getTime() && currentMinTime < end.getTime())
				start = new Date(currentMinTime)

			if (currentMinTime <= start.getTime()) this.addRoster(start, end, roster.fldOpREndTime === null)
		})
	}


	deleteRostersBetweenDates(start, end) {
		let startDate = new Date(start)
		let endDate = new Date(end)

		this.setState(prevState => {
			let newRosters = prevState.rosters.filter(roster => {
				let rosterStart = new Date(roster.fldOpRStartDate)
				let rosterTime = new Date(roster.fldOpRStartTime)
				rosterStart.setHours(rosterTime.getHours())
				rosterStart.setMinutes(rosterTime.getMinutes())
				rosterStart.setSeconds(rosterTime.getHours())

				return rosterStart.getTime() < startDate.getTime() ||
					rosterStart.getTime() > endDate.getTime()
			})

			return {
				rosters: newRosters
			}
		})
	}


	replaceRostersOnWeek(startTime, rosters, serverTime) {
		rosters.forEach((rosterList, index) => {
			let start = new Date(startTime)
			start.setDate(start.getDate() + index)
			this.replaceRostersOnDate(start, rosterList, serverTime)
		})
	}


	async deleteRosters() {
		const url = this.state.baseUrl+"roster/"+this.state.authenticated.opServId
		const options = {
			mode: 'cors',
			headers: {
				'Access-Control-Allow-Origin': '*',
				'Content-Type': 'application/json',
				'Authorization': 'Bearer '+this.state.authenticated.jwt
			},
			method: 'DELETE',
		}
		const response = await fetch(url, options)
		return response
	}


	async saveRosters() {
		let rostersToSave = this.state.rosters.filter(roster => 
			roster.fldOpRLogOn === false || roster.fldOpRLogOut === false)
		console.log(rostersToSave)
		if(rostersToSave.length === 0) {
			return await this.deleteRosters()
		}
		const url = this.state.baseUrl+"roster/"
		const options = {
				mode: 'cors',
				headers: {
					'Access-Control-Allow-Origin': '*',
					'Content-Type': 'application/json',
					'Authorization': 'Bearer '+this.state.authenticated.jwt
				},
				method: 'POST',
				body: JSON.stringify(rostersToSave)
		}
		const response = await fetch(url, options)
		return response
	}


	render() {
		const loginContainer = <Login
			service={this.state.service.name}
			services={this.state.services}
			baseUrl={this.state.baseUrl}
			setAuthenticated={this.setAuthenticated}
			getRosters={this.getRosters}
			setServices={this.setServices}
			handleServiceChange={this.handleServiceChange}
			handleLangChange={this.handleLangChange}
			authenticated={this.state.authenticated} />

		const calendarContainer = <Calendar
			baseUrl={this.state.baseUrl}
			deleteRostersBetweenDates={this.deleteRostersBetweenDates}
			replaceRostersOnWeek={this.replaceRostersOnWeek}
			replaceRostersOnDate={this.replaceRostersOnDate}
			minHour={this.state.service.minHour}
			maxHour={this.state.service.maxHour}
			cssTheme={this.state.service.theme}
			rosters={this.state.rosters}
			deleteRoster={this.deleteRoster}
			addRoster={this.addRoster}
			saveRosters={this.saveRosters}
			getRosters={this.getRosters} />

		const homeContainer = <Home 
			rosters={this.state.rosters}
			deleteRoster={this.deleteRoster}
			saveRosters={this.saveRosters}
			addRoster={this.addRoster} />

		return (
			<div className={this.state.service.theme}>
				<Router>
					{this.state.authenticated ?
						<Toolbar
							logOut={this.logOut}
							handleLangChange={this.handleLangChange}
						/> 
						: <Redirect to="/login" 
						/>
						}

					<Switch>
						<Route path="/login">
							{loginContainer}
						</Route>
						<Route exact path="/">
							{homeContainer}
						</Route>
						<Route path="/calendar">
							{calendarContainer}
						</Route>
						<Route path="/information">
							<Information />
						</Route>
						<Route path="/home">
							{homeContainer}
						</Route>
					</Switch>
				</Router>
			</div>
		);
	}
}